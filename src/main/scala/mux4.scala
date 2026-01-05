import chisel3._
import chisel3.util._

class mux4() extends Module {
    val io = IO(new Bundle {
        val sel = Input(UInt(2.W))

        val a0 = Input(UInt(32.W))
        val a1 = Input(UInt(32.W))
        val a2 = Input(UInt(32.W))
        val a3 = Input(UInt(32.W))


        val y = Output(UInt(32.W))
    })

    io.y := 1.U
    switch(io.sel) {
      is("b00".U) { io.y := io.a0 }
      is("b01".U) { io.y := io.a1 }
      is("b10".U) { io.y := io.a2 }
      is("b11".U) { io.y := io.a3 }
    }
}



