import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec


class CPUBlinkTest extends AnyFlatSpec with ChiselScalatestTester {
  "CPUBlinkTest" should "pass" in {
    test(new CPU("src/test/scala/programs/Blink.hex")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(200)

      dut.io.regs(7).expect(BigInt("00000001",16)) //Test Blink

      //addi x2, x0, 0x100     # <-- IMPORTANT: give x2 a valid RAM base in your design
      //addi x5, x0, 0x020
      //addi x6, x0, 0
      //addi x7, x0, 0

      //loop:
      //  beq  x5, x6, done
      //addi x6, x6, 1
      //addi x0, x0, 0
      //beq  x0, x0, loop

      //done:
      //  addi x6, x0, 0
      //addi x7, x7, 1
      //sw   x7, -4(x2)
      //beq  x0, x0, loop

    }
  }
}
