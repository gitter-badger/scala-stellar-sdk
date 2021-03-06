package stellar.sdk.model

import java.time.Instant

import cats.data.State
import stellar.sdk.model.xdr.{Decode, Encodable}

case class TimeBounds(start: Instant, end: Instant) extends Encodable {
  require(start.isBefore(end))

  def encode: Stream[Byte] = {
    import stellar.sdk.model.xdr.Encode._

    instant(start) ++ instant(end)
  }
}

object TimeBounds {
  def decode: State[Seq[Byte], TimeBounds] = for {
    start <- Decode.instant
    end <- Decode.instant
  } yield TimeBounds(start, end)
}
