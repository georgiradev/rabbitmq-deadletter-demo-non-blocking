package com.example.rabbitmqdeadletterdemononblocking.util;

public class Constants {

    public static final String EXCHANGE_NAME = "exchange";
    public static final String PRIMARY_QUEUE = "primaryQueue";
    public static final String WAIT_QUEUE = PRIMARY_QUEUE + ".waitingQueue";
    public static final String STORAGE_QUEUE = PRIMARY_QUEUE + ".storageQueue";
    public static final String PRIMARY_ROUTING_KEY = "primaryRoutingKey";
    public static final int RETRIES_COUNT_LIMIT = 3;

    private Constants() {}
}
