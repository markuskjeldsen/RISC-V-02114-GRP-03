import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class RipesAddiTest extends AnyFlatSpec with ChiselScalatestTester {
  "RipesAddiTest" should "pass" in {
    test(new CPU("src/test/scala/RipesTestPrograms/RipesAddi.hex",true)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(85)

      dut.io.regs.get(10).expect(0) //Test Blink

    }
  }
}
