package com.planetholt.itunes

import java.time.{ZoneOffset, ZonedDateTime}
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor

import org.json4s.{CustomSerializer, JString}

object ISODateTime {
  def format(temporal: TemporalAccessor) = formatter.format(temporal)

  private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC)
}

object ZonedDateTimeSerializer extends CustomSerializer[ZonedDateTime](_ ⇒
  ({
    case x: JString ⇒ ZonedDateTime.parse(x.s)
  }, {
    case x: ZonedDateTime ⇒ JString(ISODateTime.format(x))
  })
)
