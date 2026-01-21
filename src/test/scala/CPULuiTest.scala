import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class CPULuiTest extends AnyFlatSpec with ChiselScalatestTester {
  "CPULuiTest" should "pass" in {
    test(new CPU("src/test/scala/programs/Lui.hex",true)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(20)

      dut.io.regs.get(12).expect(BigInt("0000a002",16)) //Test LUI

      //_start:
      //  addi x1, x0, 0
      //addi x5, x0, 0

      //  jal  x1, jal_target
      //addi x0, x0, 0
      //addi x0, x0, 0

      //addi x5, x0, 1

      //end:
      //  jal x0, end
      //  addi x0, x0, 0
      //addi x0, x0, 0

      //jal_target:
      //  addi x6, x0, 42

      //jalr x0, x1, 0



    }
  }
}
