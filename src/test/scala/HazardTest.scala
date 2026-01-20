import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class HazardTest extends AnyFlatSpec with ChiselScalatestTester {
  "HazardTest" should "pass" in {
    test(new CPU("src/test/scala/programs/HazardTest.hex",true)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(20)
      dut.io.regs.get(10).expect(2)
      dut.io.regs.get(11).expect(6)
      // addi x10, x0, 2
      // add  x11, x10, x10
      // add  x11, x10, x11
    }
  }
}
