import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class CPULWSWTest extends AnyFlatSpec with ChiselScalatestTester {
  "CPULWSW" should "pass" in {
    test(new CPU("src/test/scala/programs/lwsw.hex")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(30)

      dut.io.regs(11).expect(30)
    }
  }
}
