# sam-rds-vpc-seed.g8
A template project for quickly creating an AWS Lambda which connects to an AWS RDS in a VPC and has an API GW event handler.

For more information see [sbt-sam](https://github.com/dnvriend/sbt-sam)

## Usage
Create a new template project by typing:

```
sbt new dnvriend/sam-rds-vpc-seed.g8
```

## Usage
- To deploy the project type `samDeploy`
- To remove the project type `samRemove`
- To get deployment information like available endpoints and stack information, type `samInfo`

## Lambda Configuration
Lets create the following Lambda:

```scala
@AmazonS3FullAccess
@AWSLambdaVPCAccessExecutionRole
@CloudWatchLogsFullAccess
@VPCConf(id = "MyFirstVpc")
@HttpHandler(
  path = "/insert",
  method = "put"
)
object InsertPersonLambda extends ApiGatewayHandler {
  override def handle(request: HttpRequest, ctx: SamContext): HttpResponse = {
    
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
```

### Policies
```scala
@AmazonS3FullAccess
@AWSLambdaVPCAccessExecutionRole
@CloudWatchLogsFullAccess
```
These annotations makes sure that the Lambda can access: AWS S3, AWS CloudWatch and AWS VPC.

### VPC Configuration
```scala
@VPCConf(id = "MyFirstVpc")
```
This annotation tells the Lambda that it should be able to access a VPC with id 'MyFirstVPC'.
The id 'MyFirstVPC' is a reference to a VPC Lightbend config object (sam.conf) with the same name, it has the following format:

```scala
vpcs {
  MyFirstVpc {
    subnet-ids = ["subnet-1", "subnet-2", "subnet-3"],
    security-group-ids = ["sg-1"]
  }
}
```

### Event handling
```scala
@HttpHandler(
  path = "/insert",
  method = "put"
)
```
This annotation creates an API Gateway event handler for the lambda, which listens to PUT requests on path '/insert'.

## RDS Configuration
If you want SAM to deploy a AWS RDS instance, you can achieve this by adding the following object to 'sam.conf':
```scala
rds {
  MyFirstRDS {
    db-instance-identifier = "myfirstsamdb"
    db-name = "samdb"
    allocated-storage = 5
    db-instance-class = "db.t2.micro"
    engine = "postgres"
    engine-version = "9.6.5"
    storage-type = "standard"
    master-username = "sam"
    master-password = "sam"
    multi-az = false,
    port = "5432"
    publicly-accessible = true,
    vpc-security-groups = ["sg-1"]
  }
}
```
See the [RDS CloudFormation Reference](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-rds-database-instance.html) for the allowed values.

## Information about the RDS instance
When a RDS is deployed using SAM, execute the `samInfo` command in SBT to see the following:
```
[info] RDS Instances:
[info] * foo: 
[info]   - DatabaseInstanceIdentifier: sam-s3-handler-dev-foo
[info]   - DatabaseInstanceClass: db.t2.micro
[info]   - DatabaseName: martijndb
[info]   - AllocatedStorage: 5
[info]   - Endpoint: sam-s3-handler-dev-foo.c5mvtqg6mxyp.eu-west-1.rds.amazonaws.com
[info]   - Engine: postgres
[info]   - EngineVersion: 9.6.5
[info]   - Port: 5432
[info]   - Publicly Accessible: true
[info]   - Status: available
[info]   - SecurityGroup: []
[info]   - StorageType: standard
[info]   - VPCSecurityGroup: [{VpcSecurityGroupId: sg-b1e995d6,Status: active}]
[info]          
[info] Endpoints:
[info] PUT - https://oxt8lkg617.execute-api.eu-west-1.amazonaws.com/dev/insert
[info]       
```

Have fun!