package net.weibeld.test.core;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Delivery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

class MessageHandler {

    private final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    private Channel channel;
    private String queue;

    MessageHandler(Channel channel, String queue) {
        this.channel = channel;
        this.queue = queue;
        try {
            declareQueue();
            startConsumer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void declareQueue() throws IOException {
        logger.info("Declaring queue \"" + queue + "\"");
        channel.queueDeclare(queue, false, true, false, null);
    }

    private void startConsumer() throws IOException {
        logger.info("Starting to listen for messages on queue \"" + queue + "\"");
        channel.basicConsume(queue, true, this::handleMessage, this::handleCancelConsumer);
    }

    private void handleMessage(String consumerTag, Delivery delivery) {
        String body = new String(delivery.getBody());
        logger.info("Receiving message: " + body);
        sendResponse((body + " RESPONSE").getBytes(), delivery.getProperties().getReplyTo());
    }

    private void sendResponse(byte[] body, String queue) {
        logger.info("Sending response: " + new String(body));
        try {
            channel.basicPublish("", queue, null, body);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleCancelConsumer(String consumerTag) {}
}
