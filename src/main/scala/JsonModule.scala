package com.tbrown.twitterStream

import org.joda.time.format.DateTimeFormat
import org.joda.time.{DateTime, DateTimeZone}
import jsonz._
import jsonz.joda.JodaTimeFormats

trait JsonModule extends DefaultFormats
    with ProductFormats
    with LazyFormat
    with JodaTimeFormats
    with EnumerationFormats {

  type Reads[A] = jsonz.Reads[A]
  type Writes[A] = jsonz.Writes[A]
  type Format[A] = jsonz.Format[A]
  type JsValue = jsonz.JsValue

  val JsFailure = jsonz.JsFailure

  def toJson[T](t: T)(implicit writes: Writes[T]): JsValue = Jsonz.toJson(t)
  def stringify(js: JsValue) = Jsonz.stringify(js)
  def fromJsonStr[T: Reads](s: String): Option[T] = Jsonz.fromJsonStr[T](s).toOption
  def toJsonStr[T: Writes](t: T) = Jsonz.toJsonStr(t)
}

object JsonModule extends JsonModule
