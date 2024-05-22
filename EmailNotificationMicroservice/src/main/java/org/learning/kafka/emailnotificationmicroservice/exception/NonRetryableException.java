package org.learning.kafka.emailnotificationmicroservice.exception;

//When this exception is thrown, consumer won't retry
//consuming message.
public class NonRetryableException extends RuntimeException {
    public NonRetryableException(String message) {
        super(message);
    }

    public NonRetryableException(Throwable cause) {
        super(cause);
    }
}
