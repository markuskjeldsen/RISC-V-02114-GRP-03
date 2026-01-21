import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class CPUJalrTest extends AnyFlatSpec with ChiselScalatestTester {
  "CPUJalrTest" should "pass" in {
    test(new CPU("src/test/scala/programs/Jalr.hex",true)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(30)

      dut.io.regs.get(5).expect(BigInt("00000001",16)) //Test Jalr
      dut.io.regs.get(6).expect(BigInt("0000002a",16)) //Test Jal

      //_start:
      //  addi x1, x0, 0
      //addi x5, x0, 0

      //  jal  x1, jal_target

      //addi x5, x0, 1

      //end:
      //  j end

      //jal_target:
      //addi x6, x0, 42

      //# Return to caller
      //jalr x0, x1, 0

    }
  }
}
