import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class RipesSLLTest extends AnyFlatSpec with ChiselScalatestTester {
  "RipesSLLTest" should "pass" in {
    test(new CPU("src/test/scala/RipesTestPrograms/RipesSLL.hex",true)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(175)

      dut.io.regs.get(10).expect(0) //Test Blink

    }
  }
}

