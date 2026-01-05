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

      dut.io.sel.poke(2) // multiply
      dut.clock.step(1)
      dut.io.out.expect(25)

      dut.io.sel.poke(3) // division
      dut.clock.step(1)
      dut.io.out.expect(1)


      // with negative numbers now
      dut.io.a0.poke(-5)
      dut.io.a1.poke(-5)
      dut.io.sel.poke(0) // add
      dut.clock.step(1)
      dut.io.out.expect(-10)

      dut.io.sel.poke(1) // subtract
      dut.clock.step(1)
      dut.io.out.expect(0)

      dut.io.sel.poke(2) // multiply
      dut.clock.step(1)
      dut.io.out.expect(25)

      dut.io.sel.poke(3) // division
      dut.clock.step(1)
      dut.io.out.expect(1)

      // with negative and positive numbers now
      dut.io.a0.poke(-5)
      dut.io.a1.poke(5)
      dut.io.sel.poke(0) // add
      dut.clock.step(1)
      dut.io.out.expect(0)

      dut.io.sel.poke(1) // subtract
      dut.clock.step(1)
      dut.io.out.expect(-10)

      dut.io.sel.poke(2) // multiply
      dut.clock.step(1)
      dut.io.out.expect(-25)

      dut.io.sel.poke(3) // division
      dut.clock.step(1)
      dut.io.out.expect(-1)


      dut.clock.step(10)
    }
  }
}
