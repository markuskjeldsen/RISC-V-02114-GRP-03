import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class CPUJalTest extends AnyFlatSpec with ChiselScalatestTester {
  "CPUJalTest" should "pass" in {
    test(new CPU("src/test/scala/programs/Jal.hex",true)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(22)

      //dut.io.regs(10).expect(BigInt("00000001",16)) //Test Jalr
      dut.io.regs.get(11).expect(BigInt("0000002a",16)) //Test Jal

      //start:
      //  addi x10, x0, 0
      //addi x5, x0, 0

      //  jal  x10, jal_target
      //addi x0, x0, 0
      //addi x0, x0, 0

      //addi x5, x0, 1

      //end:
      //  jal x0, end
      //  addi x12, x0, 42
      //addi x0, x0, 0

      //jal_target:
      //  addi x11, x0, 42

      //jal x0, -16

    }
  }
}
