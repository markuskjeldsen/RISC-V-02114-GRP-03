import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class RipesSLTUTest extends AnyFlatSpec with ChiselScalatestTester {
  "RipesSLTUTest" should "pass" in {
    test(new CPU("src/test/scala/RipesTestPrograms/RipesSLTU.hex",true)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(150)

      dut.io.regs.get(10).expect(0)

    }
  }
}

