package util

import java.io.StringWriter

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper

import scala.reflect.{ClassTag, Manifest}

object JsonUtil {

  val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)
  mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

  def toJson[T](data: T): String = {
    val out = new StringWriter()
    mapper.writeValue(out, data)
    out.toString
  }

  def fromJson[T: ClassTag](json: String)(implicit m: Manifest[T]): T =
    mapper.readValue[T](json)
}
