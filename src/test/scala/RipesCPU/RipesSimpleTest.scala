import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class RipesSimpleTest extends AnyFlatSpec with ChiselScalatestTester {
  "RipesSimpleTest" should "pass" in {
    test(new CPU("src/test/scala/RipesTestPrograms/RipesSimple.hex",true)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(15)

      dut.io.regs.get(10).expect(0)

    }
  }
}

