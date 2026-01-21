import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class CPUBranchTest extends AnyFlatSpec with ChiselScalatestTester {
  "CPUBranchTest" should "pass" in {
    test(new CPU("src/test/scala/programs/Branch.hex", true)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(60)

      dut.io.regs.get(15).expect(BigInt("00000010",16))
      dut.io.regs.get(16).expect(BigInt("00000011",16))
      dut.io.regs.get(17).expect(BigInt("00000012",16))
      dut.io.regs.get(18).expect(BigInt("00000013",16))
      dut.io.regs.get(19).expect(BigInt("00000014",16))
      dut.io.regs.get(20).expect(BigInt("FFFFFFFF",16))

      //addi x15, x0, 0

      //addi x10,  x0, 5
      //addi x11,  x0, 5
      //addi x12,  x0, 6
      //addi x13,  x0, -1
      //addi x14,  x0, 1

      //# NOPs to avoid hazards
      //  addi x0, x0, 0
      //addi x0, x0, 0
      //addi x0, x0, 0

      //beq  x10, x11, beq_ok
      //beq  x0, x0, fail

      //beq_ok:
      //  addi x0, x0, 0
      //addi x0, x0, 0
      //addi x0, x0, 0
      //addi x15, x0, 16

      //bne  x10, x12, bne_ok
      //beq  x0, x0, fail

      //bne_ok:
      //  addi x0, x0, 0
      //addi x0, x0, 0
      //addi x0, x0, 0
      //addi x16, x0, 17

      //blt  x13, x14, blt_ok
      //beq  x0, x0, fail

      //blt_ok:
      //  addi x0, x0, 0
      //addi x0, x0, 0
      //addi x0, x0, 0
      //addi x17, x0, 18

      //bge  x14, x13, bge_ok
      //beq  x0, x0, fail

      //bge_ok:
      //  addi x0, x0, 0
      //addi x0, x0, 0
      //addi x0, x0, 0
      //addi x18, x0, 19

      //bltu x13, x14, fail

      //addi x0, x0, 0
      //addi x0, x0, 0
      //addi x0, x0, 0
      //addi x19, x0, 20

      //bgeu x13, x14, pass
      //beq  x0, x0, fail

      //pass:
      //  addi x0, x0, 0
      //addi x0, x0, 0
      //addi x0, x0, 0

      //addi x20, x0, -1
      //beq  x0, x0, done

      //fail:
      //  addi x0, x0, 0
      //addi x0, x0, 0
      //addi x0, x0, 0

      //addi x15, x0, 0

      //done:
      //  beq  x0, x0, done

    }
  }
}
