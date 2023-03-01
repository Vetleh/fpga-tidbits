package fpgatidbits.profiler

import chisel3._
import chisel3.util._

class StateProfiler(StateCount: Int) extends Module {
  val io = IO(new Bundle {
    val start = Input(Bool())
    val probe = Input(UInt(32.W))
    val count = Output(UInt(32.W))
    val sel = Input(UInt(log2Up(StateCount).W))
  })

  // create profiling registers for keeping state counts
  val regStateCount = RegInit(VecInit(Seq.fill(StateCount)(0.U(32.W))))
  // register input state before treatment
  val regInState = RegNext(io.probe, 0.U(32.W))

  // finite state machine for control
  val sIdle :: sRun :: sFinished :: Nil = Enum(3)
  val regState = RegInit(sIdle)

  // default outputs
  io.count := regStateCount(io.sel)

  switch(regState) {
    is(sIdle) {
      when(io.start) {
        // move to running state
        regState := sRun
        // reset all profiling registers
        for (i <- 0 until StateCount) {
          regStateCount(i) := 0.U
        }
      }
    }

    is(sRun) {
      // TODOv2 RegNext may be a bad fix here
      regStateCount(regInState) := regStateCount(regInState) + 1.U
      // finish profiling when start goes low
      when(!io.start) {
        regState := sIdle
      }
    }
  }
}
