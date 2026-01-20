import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class RipesxoriTest extends AnyFlatSpec with ChiselScalatestTester {
  "RipesxoriTest" should "pass" in {
    test(new CPU("src/test/scala/RipesTestPrograms/Ripesxori.hex",true)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(80)

      dut.io.regs.get(10).expect(0) //Test Blink

    }
  }
}
