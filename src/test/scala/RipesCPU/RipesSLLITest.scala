import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class RipesSLLITest extends AnyFlatSpec with ChiselScalatestTester {
  "RipesSLLITest" should "pass" in {
    test(new CPU("src/test/scala/RipesTestPrograms/RipesSLLI.hex",true)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(140)

      dut.io.regs.get(10).expect(0)

    }
  }
}

