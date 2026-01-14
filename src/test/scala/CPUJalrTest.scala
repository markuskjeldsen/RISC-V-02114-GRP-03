import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class CPUJalrTest extends AnyFlatSpec with ChiselScalatestTester {
  "CPUJalrTest" should "pass" in {
    test(new CPU("src/test/scala/programs/Jalr.hex")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(30)

      dut.io.regs(10).expect(BigInt("FFFFFFFF",16)) //Test Jalr

      //addi x10, x0, 0         # default FAIL

      //# Build target address in x1 (PC-relative, no pseudo)
      //auipc x1, 0
      //addi  x1, x1, target

      //# NOPs so x1 is definitely written before jalr reads it
      //  addi x0, x0, 0
      //addi x0, x0, 0
      //addi x0, x0, 0
      //addi x0, x0, 0
      //addi x0, x0, 0

      //here:
      //  addi x0, x0, 0          # nop
      //addi x0, x0, 0          # nop
      //addi x0, x0, 0          # nop

      //jalr x0, x1, 0          # jump to target using x1

      //# If jalr fails, we fall through to fail
      //fail:
      //  addi x10, x0, 0
      //beq  x0, x0, done

      //target:
      //  addi x10, x0, -1        # PASS -> 0xFFFFFFFF

      //done:
      //  beq  x0, x0, done
    }
  }
}
