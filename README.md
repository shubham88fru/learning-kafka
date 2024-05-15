# learning-kafka
- A microservice needs to send a request or message to multiple other services. In such cases, a direct request reponse model isn't suitable. We need a message broker and a publish-subscribe model.

- Apache Kafka is a distributed event streaming platform that is used to collect, process, store, and integrate data at scale.
- Producer: Produces events.
- Consumer: Consumes events.
- In apache kafka, a broker is a server that performs critical functions like managing topics, handles the storage of data into
topic partitions, manages replication of data for fault tolerance, and serves client requests (from both Producer and Consumers).
- Topics are replicated across multiple brokers for resilience.
- Inside the topics, published events are stored in multiple partitions.
- Broker --> Topic --> Partitions --> Event.
- Producer produces an event --> the event is stored in Kafka topic (on a broker, and them replicated across other brokers) --> Consumers can now consume the event.
- Think of partitions as queues. Each topic on a broker has multiple partitions (queues) where events are queued in order.
- Kafka brokers follow the leader-follower model. In the cluster, there's on Leader and multiple followers. Leader manages
all the heavy lifting, and followers are mainly used for replication.
- Howerver, an importatnt point to note is that each kafka broker can act as a leader and follower at the same time. Leader and followers are per partition. i.e. for each partition in a topic, kafka assigns a broker to be the leader and others to be follower. Therefore, a borker can be a leader for a particular partition but follower for other.

- Kafka Message:
	1. Key
	2. Event
	3. Timestamp
	4. Headers

- Kafak topic cli is a command line utility to interact with kafka topics.
- Messages to topic sent with the same key are stored in the same partition and are ordered. And so,
if we want to send messages on a topic in order, we need to send them with the same key.

- A kafka producer can send messages to broken either synchronously or asynchronously. When sending a message synchronously, the producer service will wait for an ack from the broker before moving forward. While when sending asynchronously, its like a fire and forget scheme.
