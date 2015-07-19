package com.tbrown.twitterStream
import org.joda.time.format.DateTimeFormat
import org.joda.time.{DateTime, DateTimeZone}
import spray.json.DefaultJsonProtocol
import spray.json._
import spray.json.deserializationError
import spray.json.{ JsString, JsValue, RootJsonFormat, DefaultJsonProtocol }
import DefaultJsonProtocol._

object DateTimeJsonProtocol extends DefaultJsonProtocol {//specific to Twitter's datetime
  val format = "EE MMM d HH:mm:ss.SSS Z yyyy"
  val formatter = DateTimeFormat.forPattern(format)
  private def parseDate(date: String): DateTime = {
    DateTime.parse(date, formatter)
  }
  implicit object DateTimeJsonFormat extends RootJsonFormat[DateTime] {
    def write(c: DateTime) = JsString(formatter.print(c))

    def read(json: JsValue) = json match {
      case JsString(s) => parseDate(s)
      case _ => throw new DeserializationException("Error info you want here ...")
    }
  }
}
