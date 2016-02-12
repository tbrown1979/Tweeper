package com.tbrown.twitterStream

import org.joda.time.format.DateTimeFormat
import org.joda.time.{DateTime, DateTimeZone}
import jsonz._
import jsonz.joda.JodaTimeFormats
import scalaz._
import scalaz.Validation._
import scalaz.syntax._

trait JsonModule extends DefaultFormats
    with ProductFormats
    with LazyFormat
//    with JodaTimeFormats
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


  
  val format = "EE MMM d HH:mm:ss Z yyyy"
  val formatter = DateTimeFormat.forPattern(format)
  def parseDate(date: String): DateTime = DateTime.parse(date, formatter)
  def formatDateTime(date: DateTime) = formatter.print(date)
  implicit object DateTimeJsonFormat extends Format[DateTime] {
    def writes(d: DateTime) = JsString(formatDateTime(d))

    def reads(js: JsValue) = js match {
      case JsString(s) => success(parseDate(s))
      case _ => JsFailure.jsFailureValidationNel("Not a valid datetime.")
    }
  }

}

object JsonModule extends JsonModule
