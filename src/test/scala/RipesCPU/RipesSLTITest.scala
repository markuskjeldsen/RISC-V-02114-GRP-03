import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class RipesSLTITest extends AnyFlatSpec with ChiselScalatestTester {
  "RipesSLTITest" should "pass" in {
    test(new CPU("src/test/scala/RipesTestPrograms/RipesSLTI.hex",true)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(110)


    }
  }
}

