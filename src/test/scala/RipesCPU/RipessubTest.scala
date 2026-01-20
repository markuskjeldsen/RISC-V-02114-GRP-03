import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class RipessubTest extends AnyFlatSpec with ChiselScalatestTester {
  "RipessubTest" should "pass" in {
    test(new CPU("src/test/scala/RipesTestPrograms/Ripessub.hex",true)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(138)

      dut.io.regs.get(10).expect(0) //Test Blink

    }
  }
}
