import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class CPUAddiTest extends AnyFlatSpec with ChiselScalatestTester {
  "CPUAddi" should "pass" in {
    test(new CPU("src/test/scala/programs/addi.hex")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(20)
      // addi x10, x0, 30
      // addi x0, x0, 0
      // addi x0, x0, 0
      // addi x10, x10, 34
      // addi x0, x0, 0
      // addi x0, x0, 0
      // addi x0, x0, 0
      // this program adds 30 and 34 into register x10
      dut.io.regs(10).expect(64)
    }
  }
}
