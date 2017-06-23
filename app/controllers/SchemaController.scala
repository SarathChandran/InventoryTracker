package controllers

import java.io.File

import scalikejdbc._
import play.api.mvc.{Action, Controller}


/**
  * Created by sarchandran on 6/24/17.
  */
object SchemaController extends Controller {

  implicit val session = AutoSession

  def createSchema = Action {

    sql"""
          CREATE TABLE IF NOT EXISTS InventoryLog (
             inventory_id serial primary key not null,
             object_id smallint not null,
             object_type varchar(20) not null,
             timestamp bigint not null,
             object_changes json not null
         );
      """.execute().apply()

    Ok("Created")
  }


  def populateSchema(path: String) = {
    SQL(s"""
         copy InventoryLog(object_id, object_type, timestamp, object_changes)
         from \'$path\' DELIMITER ',' ESCAPE '\\' CSV HEADER;

      """).execute().apply()
  }


}
