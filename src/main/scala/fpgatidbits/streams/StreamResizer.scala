package fpgatidbits.streams

import chisel3._
import chisel3.util._
import fpgatidbits.axi._

class StreamResizer(inWidth: Int, outWidth: Int) extends Module {
  val io = IO(new Bundle {
    val in = Flipped(new AXIStreamIF(UInt(inWidth.W)))
    val out = new AXIStreamIF(UInt(outWidth.W))
  })
  if (inWidth == outWidth) {
    // no need for any resizing, directly connect in/out
    io.out.valid := io.in.valid
    io.out.bits := io.in.bits
    io.in.ready := io.out.ready
  } else if (inWidth < outWidth) {
    Predef.assert(outWidth % inWidth == 0)
    // TODO what is the third variable? seems redundant?
    StreamUpsizer(io.in, outWidth, io.in) <> io.out
  } else if (inWidth > outWidth) {
    Predef.assert(inWidth % outWidth == 0)
    // TODO same as above
    StreamDownsizer(io.in, outWidth, io.in) <> io.out
  }
}
