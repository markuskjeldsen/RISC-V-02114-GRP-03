import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class ForwardTest extends AnyFlatSpec with ChiselScalatestTester {
  "CPUForwardTest" should "pass" in {
    test(new CPU("src/test/scala/programs/Forward.hex",true)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(30)

      dut.io.regs.get(12).expect(BigInt("0000000C",16)) //Test Jalr

      //addi x6, x0, 7
      //addi x7, x0, 5
      //addi x8, x0, 12

    }
  }
}
