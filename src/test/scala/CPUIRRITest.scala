import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class CPUIRRITest extends AnyFlatSpec with ChiselScalatestTester {
  "CPUIRRITest" should "pass" in {
    test(new CPU("src/test/scala/programs/CPUIRRI.hex")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(85)
      dut.io.regs(1).expect(5)
      dut.io.regs(2).expect(3)
      dut.io.regs(3).expect(8)
        dut.io.regs(4).expect(2)
      dut.io.regs(5).expect(40) //?
      dut.io.regs(6).expect(0)
      dut.io.regs(7).expect(0)
      dut.io.regs(8).expect(6)
        dut.io.regs(9).expect(0)
      dut.io.regs(10).expect(0)
      dut.io.regs(11).expect(7)
      dut.io.regs(12).expect(1)
      dut.io.regs(13).expect(BigInt("FFFFFFF8", 16))
      dut.io.regs(14).expect(1)
      dut.io.regs(15).expect(1)
      dut.io.regs(16).expect(0)
      dut.io.regs(17).expect(BigInt("FFFFFFFC", 16))
      dut.io.regs(18).expect(BigInt("7FFFFFFC", 16))

      //addi x1,  x0, 5
      //addi x2,  x0, 3
      //addi x0,  x0, 0
      //addi x0,  x0, 0
      //addi x0,  x0, 0
      //add  x3,  x1, x2
      //addi x0,  x0, 0
      //addi x0,  x0, 0
      //addi x0,  x0, 0
      //sub  x4,  x1, x2
      //addi x0,  x0, 0
      //addi x0,  x0, 0
      //addi x0,  x0, 0
      //sll  x5,  x1, x2
      //addi x0,  x0, 0
      //addi x0,  x0, 0
      //addi x0,  x0, 0
      //slt  x6,  x1, x2
      //addi x0,  x0, 0
      //addi x0,  x0, 0
      //addi x0,  x0, 0
      //sltu x7,  x1, x2
      //addi x0,  x0, 0
      //addi x0,  x0, 0
      //addi x0,  x0, 0
      //xor  x8,  x1, x2
      //addi x0,  x0, 0
      //addi x0,  x0, 0
      //addi x0,  x0, 0
      //srl  x9,  x1, x2
      //addi x0,  x0, 0
      //addi x0,  x0, 0
      //addi x0,  x0, 0
      //sra  x10, x1, x2
      //addi x0,  x0, 0
      //addi x0,  x0, 0
      //addi x0,  x0, 0
      //or   x11, x1, x2
      //addi x0,  x0, 0
      //addi x0,  x0, 0
      //addi x0,  x0, 0
      //and  x12, x1, x2
      //addi x0,  x0, 0
      //addi x0,  x0, 0
      //addi x0,  x0, 0
      //addi x13, x0, -8
      //addi x0,  x0, 0
      //addi x0,  x0, 0
      //addi x0,  x0, 0
      //addi x14, x0, 1
      //addi x0,  x0, 0
      //addi x0,  x0, 0
      //addi x0,  x0, 0
      //slt  x15, x13, x14
      //addi x0,  x0, 0
      //addi x0,  x0, 0
      //addi x0,  x0, 0
      //sltu x16, x13, x14
      //addi x0,  x0, 0
      //addi x0,  x0, 0
      //addi x0,  x0, 0
      //sra  x17, x13, x14
      //addi x0,  x0, 0
      //addi x0,  x0, 0
      //addi x0,  x0, 0
      //srl  x18, x13, x14
    }
  }
}