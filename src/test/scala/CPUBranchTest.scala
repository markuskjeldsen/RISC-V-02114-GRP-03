import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class CPUBranchTest extends AnyFlatSpec with ChiselScalatestTester {
  "CPUBranchTest" should "pass" in {
    test(new CPU("src/test/scala/programs/Branch.hex")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(92)

      dut.io.regs(4).expect(BigInt("FFFFFFFF",16))
      dut.io.regs(5).expect(BigInt("00000001", 16))
      dut.io.regs(13).expect(BigInt("0000000C", 16))

      //addi x1, x0, 5
      //addi x2, x0, 5
      //addi x3, x0, 6
      //addi x4, x0, -1         # 0xFFFFFFFF
      //addi x5, x0, 1

      //addi x0, x0, 0          # nop
      //addi x0, x0, 0          # nop
      //addi x0, x0, 0          # nop

      //# BEQ (taken): x1 == x2
      //beq  x1, x2, beq_ok
      //addi x10, x0, 1         # fail if executed
      //beq_ok:

      //addi x0, x0, 0          # nop
      //addi x0, x0, 0          # nop
      //addi x0, x0, 0          # nop

      //# BNE (taken): x1 != x3
      //bne  x1, x3, bne_ok
      //addi x10, x0, 2         # fail if executed
      //bne_ok:

      //addi x0, x0, 0          # nop
      //addi x0, x0, 0          # nop
      //addi x0, x0, 0          # nop

      //# BLT (taken, signed): -1 < 1
      //blt  x4, x5, blt_ok
      //addi x10, x0, 3         # fail if executed
      //blt_ok:

      //addi x0, x0, 0          # nop
      //addi x0, x0, 0          # nop
      //addi x0, x0, 0          # nop

      //# BGE (taken, signed): 1 >= -1
      //bge  x5, x4, bge_ok
      //addi x10, x0, 4         # fail if executed
      //bge_ok:

      //addi x0, x0, 0          # nop
      //addi x0, x0, 0          # nop
      //addi x0, x0, 0          # nop

      //# BLTU (NOT taken, unsigned): 0xFFFFFFFF < 1 is false
      //bltu x4, x5, bltu_bad
      //addi x0, x0, 0          # nop (delay slot filler; RISC-V has no delay slots)
      //j    bltu_ok
      //  bltu_bad:
      // addi x10, x0, 5         # fail if executed
      //bltu_ok:

      //addi x0, x0, 0          # nop
      //addi x0, x0, 0          # nop
      //addi x0, x0, 0          # nop

      //# BGEU (taken, unsigned): 0xFFFFFFFF >= 1 is true
      //bgeu x4, x5, bgeu_ok
      //addi x10, x0, 6         # fail if executed
      //bgeu_ok:

      //  done:
      //addi x13, x0, 12
    }
  }
}
