package $package$

import anorm._
import com.github.dnvriend.lambda.annotation.policy.{AWSLambdaVPCAccessExecutionRole, AmazonS3FullAccess, CloudWatchLogsFullAccess}
import com.github.dnvriend.lambda.annotation.{HttpHandler, VPCConf}
import com.github.dnvriend.lambda.{ApiGatewayHandler, HttpRequest, HttpResponse, SamContext}
import play.api.libs.json.Json

@AmazonS3FullAccess
@AWSLambdaVPCAccessExecutionRole
@CloudWatchLogsFullAccess
@VPCConf(id = "MyFirstVpc")
@HttpHandler(
  path = "/insert"
)
object InsertPersonLambda extends ApiGatewayHandler {
  override def handle(request: HttpRequest, ctx: SamContext): HttpResponse = {
    import Data._

    DB.withConnection { implicit conn =>

      val createDBStatement: SimpleSql[Row] = SQL(
        """
          |CREATE TABLE IF NOT EXISTS person
          |(
          |	id integer not null
          |		constraint person_pkey
          |			primary key,
          |	name varchar(255),
          |	age integer,
          |	city varchar(255),
          |	phone_number varchar(255)
          |);
        """.stripMargin)

      val insertStatement: SimpleSql[Row] = SQL("INSERT INTO person(id, name, age, city, phone_number) values ({id}, {name}, {age}, {city}, {phone_number})")
        .on("id" -> randomInteger, "name" -> randomString, "age" -> randomInteger, "city" -> randomString, "phone_number" -> randomString)

      createDBStatement executeUpdate()

      val insertCounter = insertStatement executeUpdate()

      HttpResponse.ok.withBody(Json.toJson(s"Inserted $insertCounter record(s)"))
    }
  }
}
