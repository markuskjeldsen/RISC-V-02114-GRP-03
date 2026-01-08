import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class CPUAddTest extends AnyFlatSpec with ChiselScalatestTester {
  "CPU" should "pass" in {
    test(new CPU("src/test/scala/programs/addi.hex")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(20)
      // this program adds 30 and 34 into register x10
      dut.registers.regs(10).expect(64)
    }
  }
}
