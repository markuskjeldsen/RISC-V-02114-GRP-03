import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class RipesOriTest extends AnyFlatSpec with ChiselScalatestTester {
  "RipesOriTest" should "pass" in {
    test(new CPU("src/test/scala/RipesTestPrograms/RipesOri.hex",true)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(66)


    }
  }
}

