import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class RipesSLTIUTest extends AnyFlatSpec with ChiselScalatestTester {
  "RipesSLTIUTest" should "pass" in {
    test(new CPU("src/test/scala/RipesTestPrograms/RipesSLTIU.hex",true)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(87)


    }
  }
}

