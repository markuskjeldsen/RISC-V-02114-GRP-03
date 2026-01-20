import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class RipesLuiTest extends AnyFlatSpec with ChiselScalatestTester {
  "RipesLuiTest" should "pass" in {
    test(new CPU("src/test/scala/RipesTestPrograms/RipesLui.hex",true)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(50)


    }
  }
}

