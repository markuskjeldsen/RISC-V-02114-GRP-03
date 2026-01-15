import chisel3._
import chisel3.util._

class Registers extends Module {
  val io = IO(new Bundle {
    val rs1     = Input(UInt(5.W))   // read address 1
    val rs2     = Input(UInt(5.W))   // read address 2
    val rd      = Input(UInt(5.W))   // write address
    val rdData  = Input(UInt(32.W))  // write data
    val regWrite = Input(Bool())     // write enable

    val rs1Data = Output(UInt(32.W)) // read data 1
    val rs2Data = Output(UInt(32.W)) // read data 2
    val regs = Output(Vec(32, UInt(32.W)))
  })

  // 32 registers of 32 bits set x2 = 4096
  val regs = RegInit(VecInit(Seq.tabulate(32) { i =>
    if (i == 2) 4096.U(32.W) else 0.U(32.W)
  }))

  io.regs := regs

  // Write logic (remains the same)
  // Writes occur at the rising edge of the clock
  when(io.regWrite && io.rd =/= 0.U) {
    regs(io.rd) := io.rdData
  }

  // --- READ LOGIC WITH FORWARDING ---

  // For rs1:
  // 1. If reading x0, always return 0.
  // 2. If reading the same address currently being written to (and it's not x0), return the new data.
  // 3. Otherwise, read from the register file.
  io.rs1Data := Mux(io.rs1 === 0.U, 0.U,
    Mux(io.regWrite && io.rs1 === io.rd, io.rdData,
      regs(io.rs1)))

  // For rs2 (Same logic):
  io.rs2Data := Mux(io.rs2 === 0.U, 0.U,
    Mux(io.regWrite && io.rs2 === io.rd, io.rdData,
      regs(io.rs2)))
}
