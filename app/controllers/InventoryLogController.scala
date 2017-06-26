package controllers

import java.io.File

import play.api.http.ContentTypes
import play.api.mvc.{Action, Controller}
import service.InventoryLogService
import util.JsonUtil

import scala.util.{Failure, Success, Try}

/**
  * Created by sarchandran on 6/24/17.
  */
object InventoryLogController extends Controller {

  def create = Action {
    InventoryLogService.createSchema
    Ok("Schema Created")
  }

  def truncate = Action {
    InventoryLogService.truncateData
    Ok("Schema Truncated")
  }

  def upload = Action(parse.multipartFormData) { implicit request =>
    request.body.files.map { file =>
      val tmpFile = file.ref.moveTo(new File(s"/tmp/${file.filename}"))
      println("File Path" + tmpFile)
      InventoryLogService.populateSchema(tmpFile)
      tmpFile.delete
    }
    Ok("File Uploaded")
  }

  def track = Action { request =>
    val trackRequest = request.body.asJson
    Try(trackRequest.map { json =>
      val objectId = (json \ "object_id").as[Int]
      val objectType = (json \ "object_type").as[String]
      val timestamp = (json \ "timestamp").as[Long]
      InventoryLogService.trackStatus(objectId, objectType, timestamp)
    }.getOrElse {
      BadRequest("Expecting application/json request body")
    }) match {
      case Success(x: Some[Any]) => Ok(JsonUtil.toJson(x.getOrElse(Map.empty))).as(ContentTypes.JSON)
      case Success(_) => Ok("No updates available")
      case Failure(_) => BadRequest("Expecting application/json request body")
    }
  }

  def details = Action {
    Ok(JsonUtil.toJson(InventoryLogService.trackDetails)).as(ContentTypes.JSON)
  }


}