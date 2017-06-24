package controllers

import java.io.{File, FileReader}

import org.postgresql.copy.CopyManager
import org.postgresql.core.BaseConnection
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

  def populateSchema(path: File) = {
    val copyCSV = SQL(s"""
         copy InventoryLog(object_id, object_type, timestamp, object_changes)
         from STDIN DELIMITER ',' ESCAPE '\\' CSV HEADER;
      """).statement
    using(ConnectionPool.borrow()) { conn =>
      new CopyManager(conn.unwrap(classOf[BaseConnection]))
        .copyIn(copyCSV, new FileReader(path))
    }
  }


}
