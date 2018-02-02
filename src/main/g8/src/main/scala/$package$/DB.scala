package $package$

import com.typesafe.config.{Config, ConfigFactory}
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

object DB {
  def toProps(config: Config): java.util.Properties = {
    val properties = new java.util.Properties
    config.entrySet.forEach((e) => properties.setProperty(e.getKey, config.getString(e.getKey)))
    properties
  }

  val config: HikariConfig = new HikariConfig(toProps(ConfigFactory.load().getObject("db").toConfig))
  val ds: HikariDataSource = new HikariDataSource(config)

  def withConnection[A](f: java.sql.Connection => A): A = {
    val conn: java.sql.Connection = ds.getConnection
    try f(conn) finally conn.close()
  }

  def shutdown() = ds.close()
}