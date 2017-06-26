package controllers

import java.io.File

import play.api.http.ContentTypes
import play.api.mvc.{Action, Controller}
import service.InventoryLogService
import util.JsonUtil

import scala.util.{Failure, Success, Try}

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
    val files = request.body.files
    Try(files.map { file =>
      val tmpFile = file.ref.moveTo(new File(s"/tmp/${file.filename}"))
      InventoryLogService.populateSchema(tmpFile)
      tmpFile.delete
    }) match {
      case Success(_) => if(files.size <= 0) BadRequest("File Not uploaded!!") else Ok("File Uploaded")
      case Failure(x) => BadRequest(s"Upload Error!! ${x.getMessage}")
    }
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
      case Failure(_) => BadRequest("Expecting all input parameters")
    }
  }

  def details = Action {
    Ok(JsonUtil.toJson(InventoryLogService.trackDetails)).as(ContentTypes.JSON)
  }


}