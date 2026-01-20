import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class RipessrliTest extends AnyFlatSpec with ChiselScalatestTester {
  "RipessrliTest" should "pass" in {
    test(new CPU("src/test/scala/RipesTestPrograms/Ripessrli.hex",true)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(138)


    }
  }
}
