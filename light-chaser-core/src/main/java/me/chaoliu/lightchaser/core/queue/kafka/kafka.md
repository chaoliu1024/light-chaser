#Producer

The producer is thread safe and sharing a single producer instance across threads will generally be faster than having multiple instances.

The **producer** consists of a pool of buffer space

# Consumer

The **consumer** is not thread-safe

Kafka maintains a numerical offset for each record in a partition. Automatically advances when calls _poll(long)_.

The **position** of the consumer gives the offset of the next record that will be given out.

Kafka uses the concept of _consumer groups_ to allow a pool of processes to divide the work of consuming and processing records.

Each Kafka consumer is able to configure a consumer group that it belongs to

