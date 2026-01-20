import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class RipesSLTTest extends AnyFlatSpec with ChiselScalatestTester {
  "RipesSLTTest" should "pass" in {
    test(new CPU("src/test/scala/RipesTestPrograms/RipesSLT.hex",true)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(160)

      dut.io.regs.get(10).expect(0) //Test Blink

    }
  }
}

