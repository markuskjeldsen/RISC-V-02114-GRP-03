import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class CPUBranchTest extends AnyFlatSpec with ChiselScalatestTester {
  "CPUBranchTest" should "pass" in {
    test(new CPU("src/test/scala/programs/Branch.hex")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(70)

      dut.io.regs(17).expect(BigInt("00000001",16))

      //dut.io.regs(15).expect(BigInt("00000010",16))
      //dut.io.regs(16).expect(BigInt("00000011",16))
      //dut.io.regs(17).expect(BigInt("00000012",16))
      //dut.io.regs(18).expect(BigInt("00000013",16))
      //dut.io.regs(19).expect(BigInt("00000014",16))
      //dut.io.regs(20).expect(BigInt("FFFFFFFF",16))

      //addi x15, x0, 0         # default FAIL = 0

      //addi x10,  x0, 5
      //addi x11,  x0, 5
      //addi x12,  x0, 6
      //addi x13,  x0, -1        # x4 = 0xFFFFFFFF
      //addi x14,  x0, 1

      //# NOPs to avoid hazards
      //  addi x0, x0, 0
      //addi x0, x0, 0
      //addi x0, x0, 0

      //# ---------------- BEQ (must be taken): x1 == x2
      //beq  x10, x11, beq_ok
      //beq  x0, x0, fail       # unconditional branch (no jal)

      //beq_ok:
      //  addi x0, x0, 0
      //addi x0, x0, 0
      //addi x0, x0, 0
      //addi x15, x0, 16

      //# ---------------- BNE (must be taken): x1 != x3
      //bne  x10, x12, bne_ok
      //beq  x0, x0, fail

      //bne_ok:
      //  addi x0, x0, 0
      //addi x0, x0, 0
      //addi x0, x0, 0
      //addi x16, x0, 17

      //# ---------------- BLT (must be taken, signed): -1 < 1
      //blt  x13, x14, blt_ok
      //beq  x0, x0, fail

      //blt_ok:
      //  addi x0, x0, 0
      //addi x0, x0, 0
      //addi x0, x0, 0
      //addi x17, x0, 18

      //# ---------------- BGE (must be taken, signed): 1 >= -1
      //bge  x14, x13, bge_ok
      //beq  x0, x0, fail

      //bge_ok:
      //  addi x0, x0, 0
      //addi x0, x0, 0
      //addi x0, x0, 0
      //addi x18, x0, 19

      //# ---------------- BLTU (must NOT be taken, unsigned): 0xFFFFFFFF < 1 is false
      //bltu x13, x14, fail

      //addi x0, x0, 0
      //addi x0, x0, 0
      //addi x0, x0, 0
      //addi x19, x0, 20

      //# ---------------- BGEU (must be taken, unsigned): 0xFFFFFFFF >= 1 is true
      //bgeu x13, x14, pass
      //beq  x0, x0, fail

      //pass:
      //  addi x0, x0, 0
      //addi x0, x0, 0
      //addi x0, x0, 0

      //addi x20, x0, -1        # SUCCESS → x13 = 0xFFFFFFFF
      //beq  x0, x0, done       # unconditional branch

      //fail:
      //  addi x0, x0, 0
      //addi x0, x0, 0
      //addi x0, x0, 0

      //addi x15, x0, 0         # FAIL → x13 = 0

      //done:
      //  beq  x0, x0, done       # infinite loop, no jal

    }
  }
}
