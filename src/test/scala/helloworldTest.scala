import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class helloworldTest extends AnyFlatSpec with ChiselScalatestTester {
  "HelloWorld " should "pass" in {
    test(new HelloWorld(50000, 9600)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to 5000 cycles
      dut.clock.setTimeout(0)

      dut.clock.step(1000)
    }
  }
}
