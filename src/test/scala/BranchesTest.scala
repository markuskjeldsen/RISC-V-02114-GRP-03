import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class BranchesTest extends AnyFlatSpec with ChiselScalatestTester {
  "Branches" should "pass" in {
    test(new Branches).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)

      dut.clock.step(5)

      // ----------------------------
      // BEQ (0)
      dut.io.a0.poke(5.U)
      dut.io.a1.poke(5.U)
      dut.io.sel.poke(0.U)
      dut.clock.step(1)
      dut.io.out.expect(true.B)

      dut.io.a0.poke(5.U)
      dut.io.a1.poke(6.U)
      dut.io.sel.poke(0.U)
      dut.clock.step(1)
      dut.io.out.expect(false.B)

      // ----------------------------
      // BNE (1)
      dut.io.a0.poke(5.U)
      dut.io.a1.poke(6.U)
      dut.io.sel.poke(1.U)
      dut.clock.step(1)
      dut.io.out.expect(true.B)

      dut.io.a0.poke(5.U)
      dut.io.a1.poke(5.U)
      dut.io.sel.poke(1.U)
      dut.clock.step(1)
      dut.io.out.expect(false.B)

      // ----------------------------
      // BLT signed (2)
      // -1 < 1  => true
      dut.io.a0.poke("hFFFFFFFF".U) // -1 signed
      dut.io.a1.poke(1.U)
      dut.io.sel.poke(2.U)
      dut.clock.step(1)
      dut.io.out.expect(true.B)

      // 1 < -1  => false
      dut.io.a0.poke(1.U)
      dut.io.a1.poke("hFFFFFFFF".U) // -1 signed
      dut.io.sel.poke(2.U)
      dut.clock.step(1)
      dut.io.out.expect(false.B)

      // ----------------------------
      // BGE signed (3)
      // -1 >= 1 => false
      dut.io.a0.poke("hFFFFFFFF".U) // -1 signed
      dut.io.a1.poke(1.U)
      dut.io.sel.poke(3.U)
      dut.clock.step(1)
      dut.io.out.expect(false.B)

      // 1 >= -1 => true
      dut.io.a0.poke(1.U)
      dut.io.a1.poke("hFFFFFFFF".U) // -1 signed
      dut.io.sel.poke(3.U)
      dut.clock.step(1)
      dut.io.out.expect(true.B)

      // ----------------------------
      // BLTU unsigned (4)
      // 0xFFFFFFFF < 1 (unsigned) => false
      dut.io.a0.poke("hFFFFFFFF".U)
      dut.io.a1.poke(1.U)
      dut.io.sel.poke(4.U)
      dut.clock.step(1)
      dut.io.out.expect(false.B)

      // 1 < 0xFFFFFFFF (unsigned) => true
      dut.io.a0.poke(1.U)
      dut.io.a1.poke("hFFFFFFFF".U)
      dut.io.sel.poke(4.U)
      dut.clock.step(1)
      dut.io.out.expect(true.B)

      // ----------------------------
      // BGEU unsigned (5)
      // 0xFFFFFFFF >= 1 (unsigned) => true
      dut.io.a0.poke("hFFFFFFFF".U)
      dut.io.a1.poke(1.U)
      dut.io.sel.poke(5.U)
      dut.clock.step(1)
      dut.io.out.expect(true.B)

      // 1 >= 0xFFFFFFFF (unsigned) => false
      dut.io.a0.poke(1.U)
      dut.io.a1.poke("hFFFFFFFF".U)
      dut.io.sel.poke(5.U)
      dut.clock.step(1)
      dut.io.out.expect(false.B)

      dut.clock.step(10)
    }
  }
}
