import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class RipesAndiTest extends AnyFlatSpec with ChiselScalatestTester {
  "RipesAndiTest" should "pass" in {
    test(new CPU("src/test/scala/RipesTestPrograms/RipesAnd.hex",true)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(65)

      dut.io.regs.get(10).expect(0)
    }
  }
}
