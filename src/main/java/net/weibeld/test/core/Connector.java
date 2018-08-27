package net.weibeld.test.core;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

class Connector {

    private final Logger logger = LoggerFactory.getLogger(Connector.class);

    private Connection connection;
    private Channel channel;
    private String amqpUri;


    Connector(String amqpUri) {
        this.amqpUri = amqpUri;
        connect();
    }

    private void connect() {
        logger.info("Trying to connect to AMQP server on " + amqpUri);
        ConnectionFactory factory = configureFactory(amqpUri);
        connection = tryUntilConnect(factory);
        channel = createChannel(connection);
    }

    private ConnectionFactory configureFactory(String amqpUri) {
        ConnectionFactory factory = new ConnectionFactory();
        try {
            factory.setUri(amqpUri);
        } catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
        return factory;
    }

    private Connection tryUntilConnect(ConnectionFactory factory) {
        Connection connection = null;
        int backoffSec = 1;
        int maxTries = 60;
        int i = 1;
        while (i <= maxTries) {
            try {
                connection = factory.newConnection();
                logger.info("Connection attempt " + i + " succeeded");
                break;
            } catch (IOException | TimeoutException e) {
                logger.info("Connection attempt " + i + " failed, trying again after " + backoffSec + " second(s)");
                sleep(backoffSec);
            }
            i++;
        }
        if (connection == null)
            throw new RuntimeException("Connection attempts still failing after " + maxTries + " tries");
        else
            return connection;
    }

    private Channel createChannel(Connection connection) {
        Channel channel = null;
        try {
            channel = connection.createChannel();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return channel;
    }

    private void sleep(int sec) {
        try {
            Thread.sleep(sec * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void disconnect() {
        logger.info("Closing connection to AMQP server");
        try {
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Channel getChannel() {
        return channel;
    }
}
