package com.company

import java.util._

import com.sksamuel.avro4s._
import org.apache.kafka.clients.consumer._
import org.apache.kafka.common.serialization._
import org.apache.kafka.streams._
import org.apache.kafka.streams.kstream._

import serde._

object ProducerExample {
  def run(inputTopic: String, streamingConfig: Properties, registryConfig: Map[String, _]): Unit = {
    implicit val keyFormat = RecordFormat[Key]
    val keySerde = new CaseClassSerde[Key](isKey = true)

    implicit val personFormat = RecordFormat[Value]
    val valueSerde = new CaseClassSerde[Value](isKey = false)

    val builder = new KStreamBuilder
    val values = builder.stream(Serdes.String(), Serdes.String(), "values")

    values
        .map[Key, Value]((key, value) => (new KeyValue(Key(key), Value(Seq(new Name(name = "dsds"))))))
        .to(keySerde, valueSerde, inputTopic)

    val streams = new KafkaStreams(builder, streamingConfig)
    streams.start()
  }
}
