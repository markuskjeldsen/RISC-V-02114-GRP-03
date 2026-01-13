import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class CPUJalTest extends AnyFlatSpec with ChiselScalatestTester {
  "CPUJalTest" should "pass" in {
    test(new CPU("src/test/scala/programs/Jal.hex")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(20)

      dut.io.regs(10).expect(BigInt("00000001",16)) //Test Jalr
      dut.io.regs(11).expect(BigInt("0000002a",16)) //Test Jal

      //_start:
      //# Clear registers
      //  addi x1, x0, 0      # ra = 0
      //addi x5, x0, 0      # t0 = 0 (status)

      //# Call function using JAL
      //  jal  x1, jal_target
      //addi x0, x0, 0
      //addi x0, x0, 0

      //addi x5, x0, 1      #Here if jalr works

      //end:
      //  jal x0, end               #infinite loop
      //  addi x0, x0, 0
      //addi x0, x0, 0

      //jal_target:
      //  addi x6, x0, 42     #Here if jal works

      //#Return
      //jalr x0, x1, 0



    }
  }
}
