package com.company

import java.util._

import com.sksamuel.avro4s._
import org.apache.kafka.clients.consumer._
import org.apache.kafka.common.serialization._
import org.apache.kafka.streams._
import org.apache.kafka.streams.kstream._
import io.confluent.kafka.serializers._
import org.apache.avro.generic._

import serde._

object StreamExample {
  def run(inputTopic: String, streamingConfig: Properties, registryConfig: Map[String, _]): Unit = {
    implicit val keyFormat = RecordFormat[Key]
    val keySerde = new CaseClassSerde[Key](isKey = true)

    implicit val personFormat = RecordFormat[Value]
    val valueSerde = new CaseClassSerde[Value](isKey = false)

    val builder = new StreamsBuilder
    val stream: KStream[Key, Value] = builder.stream(inputTopic)

    stream
        .map[Key, Value]((k, v) => {
            println(v)

            new KeyValue(k, v)
        })

    val streams: KafkaStreams = new KafkaStreams(builder.build(), streamingConfig)

    streams.start()
  }
}
