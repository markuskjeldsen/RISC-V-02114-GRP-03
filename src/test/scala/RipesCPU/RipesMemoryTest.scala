import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class RipesMemoryTest extends AnyFlatSpec with ChiselScalatestTester {
  "RipesMemoryTest" should "pass" in {
    test(new CPU("src/test/scala/RipesTestPrograms/RipesMemory.hex",true)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(61)

      dut.io.regs.get(10).expect(0)

    }
  }
}

