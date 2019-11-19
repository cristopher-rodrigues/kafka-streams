package com.company

import java.util._

import org.apache.kafka.streams._
import org.apache.kafka.clients.consumer._
import org.apache.avro.specific.SpecificRecord
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import io.confluent.kafka.serializers.{AbstractKafkaAvroSerDeConfig, KafkaAvroDeserializerConfig}
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde

// import serde._

object Main extends App {
   val KAFKA_BROKER="broker:29092"
   val KAFKA_REGISTRY_ENDPOINT="http://kafka-schema-registry:8081"
    
   val streamingConfig = {
      val props = new Properties
      props.put(StreamsConfig.APPLICATION_ID_CONFIG, "consumer-pipes-crud-to-event-611112112")
      props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKER)
      props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
      props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, KAFKA_REGISTRY_ENDPOINT)
      // props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, classOf[CaseClassSerde[_ <: Key]])
      // props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, classOf[CaseClassSerde[_ <: Value]])
      props
  }

   val registryConfig = new java.util.HashMap[String, Any]()
   registryConfig.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, KAFKA_REGISTRY_ENDPOINT)
   registryConfig.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true")
   // registryConfig.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "false")

   val inputTopic = "avro-values"

   // StreamExample.run(inputTopic, streamingConfig, registryConfig)
   ProducerExample.run(inputTopic, streamingConfig, registryConfig)
}
