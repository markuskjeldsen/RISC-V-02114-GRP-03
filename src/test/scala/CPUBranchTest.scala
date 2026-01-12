import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class CPUBranchTest extends AnyFlatSpec with ChiselScalatestTester {
  "CPUBranchTest" should "pass" in {
    test(new CPU("src/test/scala/programs/Branch.hex")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(100)

      dut.io.regs(13).expect(BigInt("FFFFFFFF",16))

      //addi x1,  x0, 5
      //addi x2,  x0, 5
      //addi x3,  x0, 6
      //addi x4,  x0, -1        # x4 = 0xFFFFFFFF
      //addi x5,  x0, 1

      //addi x0, x0, 0          # nop
      //addi x0, x0, 0          # nop
      //addi x0, x0, 0          # nop

      //# ---------------- BEQ (taken): x1 == x2
      //beq  x1, x2, beq_ok
      //jal  x0, fail
      //beq_ok:

      //  addi x0, x0, 0          # nop
      //addi x0, x0, 0          # nop
      //addi x0, x0, 0          # nop

      //# ---------------- BNE (taken): x1 != x3
      //bne  x1, x3, bne_ok
      //jal  x0, fail
      //bne_ok:

      //  addi x0, x0, 0          # nop
      //addi x0, x0, 0          # nop
      //addi x0, x0, 0          # nop

      //# ---------------- BLT (taken, signed): -1 < 1
      //blt  x4, x5, blt_ok
      //jal  x0, fail
      //blt_ok:

      //  addi x0, x0, 0          # nop
      //addi x0, x0, 0          # nop
      //addi x0, x0, 0          # nop

      //# ---------------- BGE (taken, signed): 1 >= -1
      //bge  x5, x4, bge_ok
      //jal  x0, fail
      //bge_ok:

      //  addi x0, x0, 0          # nop
      //addi x0, x0, 0          # nop
      //addi x0, x0, 0          # nop

      //# ---------------- BLTU (NOT taken, unsigned)
      //# 0xFFFFFFFF < 1  ==> false
      //bltu x4, x5, fail

      //addi x0, x0, 0          # nop
      //addi x0, x0, 0          # nop
      //addi x0, x0, 0          # nop

      //# ---------------- BGEU (taken, unsigned)
      //# 0xFFFFFFFF >= 1 ==> true
      //bgeu x4, x5, pass
      //jal  x0, fail

      //pass:
      //  addi x0, x0, 0          # nop
      //addi x0, x0, 0          # nop
      //addi x0, x0, 0          # nop

      //addi x13, x0, -1        # SUCCESS → x13 = 0xFFFFFFFF
      //jal  x0, done

      //fail:
      //  addi x0, x0, 0          # nop
      //addi x0, x0, 0          # nop
      //addi x0, x0, 0          # nop

      //addi x13, x0, 0         # FAIL → x13 = 0

      //done:
      //  jal  x0, done

    }
  }
}
