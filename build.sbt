name := "com.company"

version := "1.0"

scalaVersion := "2.13.1"

resolvers += "Confluent" at "http://packages.confluent.io/maven/"

val kafkaStreams = "org.apache.kafka" % "kafka-streams" % "1.1.0"
val avroSerializer = "io.confluent"        %  "kafka-avro-serializer" % "3.2.1"
val avro4s =         "com.sksamuel.avro4s" %% "avro4s-core"           % "3.0.0"
val avroSerde = "io.confluent" % "kafka-streams-avro-serde" % "4.1.0"  


libraryDependencies ++= Seq(
  kafkaStreams,
  avroSerializer,
  avro4s,
  avroSerde
)
