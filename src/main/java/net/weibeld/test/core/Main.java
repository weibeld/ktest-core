package net.weibeld.test.core;

public class Main {

    private static final String AMQP_URI = System.getenv("AMQP_URI");
    static {
        if (AMQP_URI == null)
            throw new RuntimeException("Must set AMQP_URI environment variable");
    }
    private static final String QUEUE = System.getenv("QUEUE");
    static {
        if (QUEUE == null)
            throw new RuntimeException("Must set QUEUE environment variable");
    }

    public static void main(String[] args) {
        Connector connector = new Connector(AMQP_URI);
        new MessageHandler(connector.getChannel(), QUEUE);
    }
}
