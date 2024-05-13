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

- Kafka Message:
	1. Key
	2. Event
	3. Timestamp
	4. Headers
