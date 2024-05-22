package org.learning.kafka.emailnotificationmicroservice.exception;

//When code throws this exception, the consumer
//will retry (with backoff) to consume the message again.
public class RetryableException extends RuntimeException {
    public RetryableException(String message) {
        super(message);
    }

    public RetryableException(Throwable cause) {
        super(cause);
    }
}
