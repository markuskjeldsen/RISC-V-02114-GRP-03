import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec


class CPUAUIPCTest extends AnyFlatSpec with ChiselScalatestTester {
  "CPUAUIPCTest" should "pass" in {
    test(new CPU("src/test/scala/programs/AUIPC.hex")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(10)

      dut.io.regs(10).expect(BigInt("0000200c",16)) //Test AUIPC

    // AUIPC x10, 0x1
    // ADDI x11, x0, 0
    // AUIPC x10, 0x2



    }
  }
}
