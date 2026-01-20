import chiseltest.{ChiselScalatestTester, WriteVcdAnnotation}
import org.scalatest.flatspec.AnyFlatSpec

class RipesAddi extends AnyFlatSpec with ChiselScalatestTester {
  "RipesCPU.RipesAddi" should "pass" in {
    test(new CPU("src/test/scala/RipesTestPrograms/RipesCPU.RipesAddi.hex",true)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(22)


    }
  }
}
