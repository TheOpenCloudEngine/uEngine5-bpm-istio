package org.uengine.five;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface Streams {
    String INPUT = "bpm-topic";

    @Input(INPUT)
    SubscribableChannel inboundGreetings();

    @Output("bpm-topic")
    MessageChannel outboundChannel();

}
