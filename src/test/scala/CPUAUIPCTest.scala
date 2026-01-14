import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec


class CPUAUIPCTest extends AnyFlatSpec with ChiselScalatestTester {
  "CPUAUIPCTest" should "pass" in {
    test(new CPU("src/test/scala/programs/AUIPC.hex")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(20)

      dut.io.regs(10).expect(BigInt("00002024",16)) //Test AUIPC



    }
  }
}
