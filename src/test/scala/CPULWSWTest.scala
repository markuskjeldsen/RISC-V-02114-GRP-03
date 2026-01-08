import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class CPULWSWTest extends AnyFlatSpec with ChiselScalatestTester {
  "CPUAdd" should "pass" in {
    test(new CPU("src/test/scala/programs/lwsw.hex")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(20)
      // addi x10, x0 ,0
      // addi x11, x0 ,0
      // addi x10, x0 , 30
      // addi x0, x0, 0
      // addi x0, x0, 0
      // addi x0, x0, 0
      // sw x10, -4(x2)
      // addi x0, x0, 0
      // addi x0, x0, 0
      // lw x11, -4(x2)
      // addi x0, x0, 0
      // addi x0, x0, 0

      // this program adds 30 and 34 into register x12
      dut.io.regs(11).expect(30)
    }
  }
}
