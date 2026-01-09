import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class HazardTest extends AnyFlatSpec with ChiselScalatestTester {
  "HazardTest" should "pass" in {
    test(new CPU("src/test/scala/programs/HazardTest.hex")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(20)
      dut.io.regs(1).expect(2)
      dut.io.regs(2).expect(6)
      // addi x1, x0, 2
      // add  x2, x1, x1
      // add  x2, x1, x2
    }
  }
}
