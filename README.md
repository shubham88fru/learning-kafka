# learning-kafka
- A microservice needs to send a request or message to multiple other services. In such cases, a direct request reponse model isn't suitable. We need a message broker and a publish-subscribe model.

- Apache Kafka is a distributed event streaming platform that is used to collect, process, store, and integrate data at scale.
- Producer: Produces events.
- Consumer: Consumes events.
- Topics are replicated across multiple brokers for resilience.
- Inside the topics, published events are stored in multiple partitions.
- Broker --> Topic --> Partitions --> Event.
- Producer produces an event --> the event is stored in Kafka topic (on a broker, and them replicated across other brokers) --> Consumers can now consume the event.
