import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class ALUTest extends AnyFlatSpec with ChiselScalatestTester {
  "ALU " should "pass" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)

      dut.clock.step(5)
      dut.io.a0.poke(5)
      dut.io.a1.poke(5)
      dut.io.sel.poke(0) // add
      dut.clock.step(1)
      dut.io.out.expect(10)

      dut.io.sel.poke(1) // subtract
      dut.clock.step(1)
      dut.io.out.expect(0)

      // with negative numbers now
      dut.io.a0.poke(5)
      dut.io.a1.poke(5)
      dut.io.sel.poke(0) // add
      dut.clock.step(1)
      dut.io.out.expect(10)


      dut.io.a0.poke(5)
      dut.io.a1.poke(6)
      dut.io.sel.poke(1) // subtract
      dut.clock.step(1)
      dut.io.out.expect(0xFFFFFFFFL.U)

      dut.io.a0.poke(1.U)
      dut.io.a1.poke(3.U)
      dut.io.sel.poke(2.U)
      dut.clock.step(1)
      dut.io.out.expect(8.U)

      dut.io.a0.poke("hFFFFFFFF".U)
      dut.io.a1.poke(1.U)
      dut.io.sel.poke(3.U)
      dut.clock.step(1)
      dut.io.out.expect(1.U)

      dut.io.a0.poke(1.U)
      dut.io.a1.poke("hFFFFFFFF".U)
      dut.io.sel.poke(3.U)
      dut.clock.step(1)
      dut.io.out.expect(0.U)

      dut.io.a0.poke("hFFFFFFFF".U)
      dut.io.a1.poke(1.U)
      dut.io.sel.poke(4.U)
      dut.clock.step(1)
      dut.io.out.expect(0.U)

      dut.io.a0.poke(1.U)
      dut.io.a1.poke("hFFFFFFFF".U)
      dut.io.sel.poke(4.U)
      dut.clock.step(1)
      dut.io.out.expect(1.U)

      dut.io.a0.poke("h0000F0F0".U)
      dut.io.a1.poke("h00000FF0".U)
      dut.io.sel.poke(5.U)
      dut.clock.step(1)
      dut.io.out.expect("h0000FF00".U)

      dut.io.a0.poke("h80000000".U)
      dut.io.a1.poke(1.U)
      dut.io.sel.poke(6.U)
      dut.clock.step(1)
      dut.io.out.expect("h40000000".U)

      dut.io.a0.poke("h80000000".U)
      dut.io.a1.poke(1.U)
      dut.io.sel.poke(7.U)
      dut.clock.step(1)
      dut.io.out.expect("hC0000000".U)

      dut.io.a0.poke("h00000F0F".U)
      dut.io.a1.poke("h000000FF".U)
      dut.io.sel.poke(8.U)
      dut.clock.step(1)
      dut.io.out.expect("h00000FFF".U)

      dut.io.a0.poke("h00000F0F".U)
      dut.io.a1.poke("h000000FF".U)
      dut.io.sel.poke(9.U)
      dut.clock.step(1)
      dut.io.out.expect("h0000000F".U)

      dut.clock.step(10)
    }
  }
}
