import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class RipesAndTest extends AnyFlatSpec with ChiselScalatestTester {
  "RipesAndTest" should "pass" in {
    test(new CPU("src/test/scala/RipesTestPrograms/RipesAnd.hex",true)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(22)


    }
  }
}
