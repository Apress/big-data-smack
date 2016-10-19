import akka.kafka._
import akka.kafka.scaladsl._
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.clients.consumer.ConsumerConfig

val consumerSettings = ConsumerSettings(system, new ByteArrayDeserializer, new StringDeserializer)
  .withBootstrapServers("localhost:9092")
  .withGroupId("group1")
  .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

//Consume messages and store a representation, including offset, in DB:
db.loadOffset().foreach { fromOffset =>
	val subscription = Subscriptions.assignmentWithOffset(new TopicPartition("topic1", 1) -> fromOffset)
    Consumer.plainSource(consumerSettings, subscription)
      .mapAsync(1)(db.save)}

//Consume messages at-most-once:
Consumer.atMostOnceSource(consumerSettings.withClientId("client1"), Subscriptions.topics("topic1"))
    .mapAsync(1) { record =>
      rocket.launch(record.value)
    }

//Consume messages at-least-once:
Consumer.committableSource(consumerSettings.withClientId("client1"), Subscriptions.topics("topic1"))
    .mapAsync(1) { msg =>
      db.update(msg.value).flatMap(_ => msg.committableOffset.commitScaladsl())
    }

//Connect a Consumer to Producer:
Consumer.committableSource(consumerSettings.withClientId("client1"))
    .map(msg =>
      ProducerMessage.Message(new ProducerRecord[Array[Byte], String]("topic2", msg.value), msg.committableOffset))
    .to(Producer.commitableSink(producerSettings))

//Consume messages at-least-once, and commit in batches:
Consumer.committableSource(consumerSettings.withClientId("client1"), Subscriptions.topics("topic1"))
    .mapAsync(1) { msg =>
      db.update(msg.value).map(_ => msg.committableOffset)
    }
    .batch(max = 10, first => CommittableOffsetBatch.empty.updated(first)) { (batch, elem) =>
      batch.updated(elem)
    }
    .mapAsync(1)(_.commitScaladsl())

//A reusable Kafka consumer:

//Consumer is represented by actor
//Create new consumer
val consumer: ActorRef = system.actorOf(KafkaConsumerActor.props(consumerSettings))

//Manually assign topic partition to it
val stream1 = Consumer
    .plainExternalSource[Array[Byte], String](consumer, Subscriptions.assignment(new TopicPartition("topic1", 1)))
    .via(business)
    .to(Sink.ignore)

//Manually assign another topic partition
val stream2 = Consumer
    .plainExternalSource[Array[Byte], String](consumer, Subscriptions.assignment(new TopicPartition("topic1", 2)))
    .via(business)
    .to(Sink.ignore)

Consumer group:

//Consumer group represented as Source[(TopicPartition, Source[Messages])]
val consumerGroup = Consumer.committablePartitionedSource(consumerSettings.withClientId("client1"), Subscriptions.topics("topic1"))
  //Process each assigned partition separately
  consumerGroup.map {
    case (topicPartition, source) =>
      source
        .via(business)
        .toMat(Sink.ignore)(Keep.both)
        .run()
  }.mapAsyncUnordered(maxPartitions)(_._2)
