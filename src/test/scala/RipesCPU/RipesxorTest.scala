import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class RipesxorTest extends AnyFlatSpec with ChiselScalatestTester {
  "RipesxorTest" should "pass" in {
    test(new CPU("src/test/scala/RipesTestPrograms/Ripesxor.hex",true)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(100)

      dut.io.regs.get(10).expect(0) //Test Blink

    }
  }
}
