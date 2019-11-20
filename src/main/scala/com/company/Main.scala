package com.company

import java.util.{Collections, Properties, List}

import io.confluent.kafka.serializers.{AbstractKafkaAvroSerDeConfig, KafkaAvroDeserializer, KafkaAvroSerializer}
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde
import org.apache.avro.Schema
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization._
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.Produced
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}

object Main extends App {
  import org.apache.kafka.streams.scala.Serdes._
  import org.apache.kafka.streams.scala.ImplicitConversions._

   val KAFKA_BROKER="broker:29092"
   val KAFKA_REGISTRY_ENDPOINT="http://kafka-schema-registry:8081"

   private val inputTopic = "inputTopic4"
   private val outputTopic = "output-topic"
   
    val builder = new StreamsBuilder

    val streamsConfiguration: Properties = {
      val p = new Properties()
      p.put(StreamsConfig.APPLICATION_ID_CONFIG, "dskdsns2kd2nsknd")
      p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKER)
      p.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, KAFKA_REGISTRY_ENDPOINT)
      p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
      p
    }

    implicit val genericAvroSerde: Serde[GenericRecord] = {
      val gas = new GenericAvroSerde
      val isKeySerde: Boolean = false
      gas.configure(Collections.singletonMap(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, KAFKA_REGISTRY_ENDPOINT), isKeySerde)
      gas
    }

    val values = builder.stream[String, GenericRecord](inputTopic)

    val oputputValues = values
        .map[String, GenericRecord]((k, v) => {
            println("__________________________________________")
            // val payload = v.get("payload");

            // println(payload) // [{"bar": "test"}]
            // println(payload.getClass()) // class org.apache.avro.generic.GenericData$Array
            // println(payload.asInstanceOf[List[Object]]) // java.lang.ClassCastException: org.apache.avro.generic.GenericData$Array cannot be cast to scala.collection.immutable.List
            // println(payload.toString().hashCode()) // 1593002213
            // println(payload.toString()) // [{"bar": "test"}]

            case class Payload(bar: String)
            // case class Value(payload: Seq[Payload])

          val mapper = new com.fasterxml.jackson.databind.ObjectMapper();
          val payload: List[Payload] = mapper.readValue(v.get("payload").toString(), classOf[List[Payload]]);

          println(payload)
          println(payload.flatten)
            // p.map((k, v) => {
            //   println(v)
            // })

            println("__________________________________________")

            (k,v)
        })

    val streams: KafkaStreams = new KafkaStreams(builder.build(), streamsConfiguration)

    streams.start()

    // {"payload": [{"bar": "test"}]}
}
