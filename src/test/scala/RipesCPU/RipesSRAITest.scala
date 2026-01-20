import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class RipesSRAITest extends AnyFlatSpec with ChiselScalatestTester {
  "RipesSRAITest" should "pass" in {
    test(new CPU("src/test/scala/RipesTestPrograms/RipesSRAI.hex",true)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(70)


    }
  }
}

