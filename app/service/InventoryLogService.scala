package service

import java.io.{File, FileReader}

import org.postgresql.copy.CopyManager
import org.postgresql.core.BaseConnection
import scalikejdbc._
import util.JsonUtil

import scala.util.{Failure, Success, Try}

object InventoryLogService {

  implicit val session = AutoSession

  def createSchema = {
    sql"""
          CREATE TABLE IF NOT EXISTS InventoryLog (
             inventory_id serial primary key not null,
             object_id smallint not null,
             object_type varchar(20) not null,
             timestamp bigint not null,
             object_changes json not null
         );
      """.execute().apply()
  }

  def truncateData = {
    sql"""
        TRUNCATE TABLE InventoryLog;
      """.execute().apply()
  }

  def populateSchema(path: File) = {
    val copyCSV = SQL(s"""
         copy InventoryLog(object_id, object_type, timestamp, object_changes)
         from STDIN DELIMITER ',' ESCAPE '\\' CSV HEADER;
      """).statement
    Try(using(ConnectionPool.borrow()) { conn =>
      new CopyManager(conn.unwrap(classOf[BaseConnection]))
        .copyIn(copyCSV, new FileReader(path))
    }) match {
      case Failure(_) => throw new Exception("Invalid Changelog File")
      case Success(_) =>
    }
  }

  def trackStatus(objectId: Int, objectType: String, timestamp: Long) = {
    sql"""
      select json_agg(object_changes) from InventoryLog
      where object_id = ? and object_type like ? and timestamp <= ?;
      """
      .bind(objectId, objectType, timestamp)
      .map(_.array("json_agg")).single().apply()
      .map(array => JsonUtil.fromJson[Array[Map[String, Any]]](array.toString).reduce(_ ++ _))
  }

  def trackDetails = {
    val objectIds = sql"""
         select distinct object_id from InventoryLog;
      """.map(_.int("object_id")).list().apply()

    val objectTypes = sql"""
         select distinct object_type from InventoryLog;
      """.map(_.string("object_type")).list().apply()
    Map("object_ids" -> objectIds, "object_types" -> objectTypes)
  }

}
