import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class RipesSrlTest extends AnyFlatSpec with ChiselScalatestTester {
  "RipesSrl" should "pass" in {
    test(new CPU("src/test/scala/RipesTestPrograms/Ripessrl.hex",true)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(250)

      dut.io.regs.get(10).expect(0) //Test Blink

    }
  }
}
