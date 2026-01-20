import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class RipesAddTest extends AnyFlatSpec with ChiselScalatestTester {
  "RipesAddTest" should "pass" in {
    test(new CPU("src/test/scala/RipesTestPrograms/RipesAdd.hex",true)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(150)


    }
  }
}

