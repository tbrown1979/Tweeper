package com.tbrown.twitterStream
import spray.json.DefaultJsonProtocol
import spray.json._
import org.joda.time.{DateTime, DateTimeZone}
import org.joda.time.format.DateTimeFormat
import spray.json.deserializationError
import spray.json.{ JsString, JsValue, RootJsonFormat, DefaultJsonProtocol }
import DefaultJsonProtocol._


object DateTimeJsonProtocol extends DefaultJsonProtocol {//specific to Twitter's datetime
  private def parseDate(date: String): DateTime = {
    val format = "EE MMM d HH:mm:ss Z yyyy"
    DateTime.parse(date, DateTimeFormat.forPattern(format))
  }
  implicit object DateTimeJsonFormat extends RootJsonFormat[DateTime] {
    def write(c: DateTime) = JsString(c.toString)

    def read(json: JsValue) = json match {
      case JsString(s) => parseDate(s)
      case _ => throw new DeserializationException("Error info you want here ...")
    }
  }
}
