import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class CPUAddTest extends AnyFlatSpec with ChiselScalatestTester {
  "CPUAdd" should "pass" in {
    test(new CPU("src/test/scala/programs/add.hex")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(11)
      // addi x10, x0, 30
      // addi x11, x0, 34
      // addi x0, x0, 0
      // addi x0, x0, 0
      // addi x0, x0, 0
      // add x12, x10, x11
      // addi x0, x0, 0
      // addi x0, x0, 0
      dut.io.regs(12).expect(64)

      // this program adds 30 and 34 into register x12
      dut.clock.step(4)
      dut.io.regs(12).expect(BigInt("FFFFFFC0",16))
    }
  }
}
