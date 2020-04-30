# Kafka Streams 

FlatMap a nested array of Records

### event topic Avro schema (input)

```json
{
	"type": "record",
	"name": "Value",
	"namespace": "com.company",
	"fields": [{
		"name": "payload",
		"type": {
			"type": "array",
			"items": [{
				"type": "record",
				"name": "Name",
				"fields": [{
					"name": "name",
					"type": "string"
				}]
			}, {
				"type": "record",
				"name": "Icon",
				"fields": [{
					"name": "icon",
					"type": "string"
				}]
			}]
		}
	}]
}
```

### event topic Avro schema (output)

```json
{
	"type": "record",
	"name": "Output",
	"namespace": "com.company",
    "fields": [{
        "name": "icon",
        "type": "string"
    }]
}
```

#### Running

```bash
docker-compose up
```

```bash
kafka-console-producer --broker-list localhost:9092 --topic values \
    --property parse.key=true \
    --property key.separator=,
```

produce: `key,value`

following the input topic `val inputTopic = "avro-values"`

```bash
kafka-avro-console-consumer --topic avro-values \
    --bootstrap-server 127.0.0.1:9092 \
    --property schema.registry.url=http://127.0.0.1:8081 --from-beginning
```

`Main.scala` => `ProducerExample.run(inputTopic, streamingConfig, registryConfig)`

```bash
docker exec -it kafka-streams_app_1 bash
```

on docker

```bash
cd /app
```

```bash
sbt
~run
```

`Main.scala` => `StreamExample.run(inputTopic, streamingConfig, registryConfig)`

### ERROR

```
java.lang.ClassCastException: [B cannot be cast to com.company.Key
[error] java.lang.ClassCastException: [B cannot be cast to com.company.Key
```

```bash
curl http://127.0.0.1:8081/subjects/avro-values-value/versions/1
```

```bash
curl http://127.0.0.1:8081/subjects/avro-values-key/versions/1
```
