package com.planetholt.itunes

import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.time.{Instant, ZoneOffset}

import org.json4s.{CustomSerializer, JString}

object ISODateTime {
  def format(temporal: TemporalAccessor) = formatter.format(temporal)

  private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC)
}

object InstantSerializer extends CustomSerializer[Instant](_ ⇒
  ({
    case x: JString ⇒ Instant.parse(x.values)
  }, {
    case x: Instant ⇒ JString(ISODateTime.format(x))
  })
)
