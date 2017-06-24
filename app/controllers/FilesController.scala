package controllers

import java.io.File

import play.api.mvc.{Action, Controller}

/**
  * Created by sarchandran on 6/24/17.
  */
object FilesController extends Controller {

  def upload = Action(parse.multipartFormData) { implicit request =>
    request.body.files.map { file =>
      val tmpFile = file.ref.moveTo(new File(s"/tmp/${file.filename}"))
      println("File Path" + tmpFile)
      SchemaController.populateSchema(tmpFile)
      tmpFile.delete
    }
    Ok("File Uploaded")
  }


}