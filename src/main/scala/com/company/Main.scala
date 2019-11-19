package com.company

import java.util.{Collections, Properties}

import io.confluent.kafka.serializers.{AbstractKafkaAvroSerDeConfig, KafkaAvroDeserializer, KafkaAvroSerializer}
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde
import org.apache.avro.Schema
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization._
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}

object Main extends App {
  import org.apache.kafka.streams.scala.Serdes._
  import org.apache.kafka.streams.scala.ImplicitConversions._

   val KAFKA_BROKER="broker:29092"
   val KAFKA_REGISTRY_ENDPOINT="http://kafka-schema-registry:8081"

    val schemaAVSC = """
    {"namespace": "com.company",
      "type": "record",
      "name": "WikiFeed",
      "fields": [
         {"name": "user", "type": "string"},
         {"name": "is_new", "type": "boolean"},
         {"name": "content", "type": ["string", "null"]}
      ]
      }
    """

    val schema: Schema = new Schema.Parser().parse(schemaAVSC)
    
   private val inputTopic = "inputTopic"
   private val outputTopic = "output-topic"

    val record: GenericRecord = {
      val r = new GenericData.Record(schema)
      r.put("user", "alice")
      r.put("is_new", true)
      r.put("content", "lorem ipsum")
      r
    }
    val inputValues: Seq[GenericRecord] = Seq(record)

    //
    // Step 1: Configure and start the processor topology.
    //
    val builder = new StreamsBuilder

    val streamsConfiguration: Properties = {
      val p = new Properties()
      p.put(StreamsConfig.APPLICATION_ID_CONFIG, "generic-avro-scala-integration-test")
      p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKER)
      p.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, KAFKA_REGISTRY_ENDPOINT)
      p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
      p
    }

    // Make an implicit serde available for GenericRecord, which is required for operations such as `to()` below.
    implicit val genericAvroSerde: Serde[GenericRecord] = {
      val gas = new GenericAvroSerde
      val isKeySerde: Boolean = false
      gas.configure(Collections.singletonMap(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, KAFKA_REGISTRY_ENDPOINT), isKeySerde)
      gas
    }

    // Write the input data as-is to the output topic.
    builder.stream[String, GenericRecord](inputTopic).to(outputTopic)

    val streams: KafkaStreams = new KafkaStreams(builder.build(), streamsConfiguration)
    streams.start()

    //
    // Step 2: Produce some input data to the input topic.
    //
    val producerConfig: Properties = {
      val p = new Properties()
      p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKER)
      p.put(ProducerConfig.ACKS_CONFIG, "all")
      p.put(ProducerConfig.RETRIES_CONFIG, "0")
      p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[ByteArraySerializer])
      p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[KafkaAvroSerializer])
      p.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, KAFKA_REGISTRY_ENDPOINT)
      p
    }
    import collection.JavaConverters._
   //  IntegrationTestUtils.produceValuesSynchronously(inputTopic, inputValues.asJava, producerConfig)

    //
    // Step 3: Verify the application's output data.
    //
   //  val consumerConfig = {
   //    val p = new Properties()
   //    p.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKER)
   //    p.put(ConsumerConfig.GROUP_ID_CONFIG, "generic-avro-scala-integration-test-standard-consumer")
   //    p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
   //    p.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[ByteArrayDeserializer])
   //    p.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[KafkaAvroDeserializer])
   //    p.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, KAFKA_REGISTRY_ENDPOINT)
   //    p
   //  }
}
