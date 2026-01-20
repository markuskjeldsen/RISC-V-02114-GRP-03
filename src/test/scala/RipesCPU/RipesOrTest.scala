import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class RipesOrTest extends AnyFlatSpec with ChiselScalatestTester {
  "RipesOrTest" should "pass" in {
    test(new CPU("src/test/scala/RipesTestPrograms/RipesOr.hex",true)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(95)

      dut.io.regs.get(10).expect(0) //Test Blink

    }
  }
}

