import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class CPUJalrTest extends AnyFlatSpec with ChiselScalatestTester {
  "CPUJalrTest" should "pass" in {
    test(new CPU("src/test/scala/programs/Jalr.hex")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(30)

      dut.io.regs(5).expect(BigInt("00000001",16)) //Test Jalr
      dut.io.regs(6).expect(BigInt("0000002a",16)) //Test Jal

      //_start:
      //# Clear registers
      //  addi x1, x0, 0      # ra = 0
      //addi x5, x0, 0      # t0 = 0 (status)

      //# Call function using JAL
      //  jal  x1, jal_target

      //# If return works, execution resumes here
      //addi x5, x0, 1      # t0 = 1 (PASS)

      //end:
      //  j end               # infinite loop

      //jal_target:
      //# If we reach here, JAL jump worked
      //addi x6, x0, 42     # t1 = 42 (marker)

      //# Return to caller
      //jalr x0, x1, 0

    }
  }
}
