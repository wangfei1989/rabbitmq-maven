package com.gper.lizi.rabbitmq;

import com.gper.lizi.util.ResourceUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.sun.org.apache.xerces.internal.dom.PSVIAttrNSImpl;

import java.io.IOException;

/**
 * lizi
 * 2019/8/27
 */
public class MsgConsumer {

    private ConnectionFactory factory;

    private Connection connection;

    private Channel channel;


    private void init() throws Exception {
        factory = new ConnectionFactory();
        factory.setUri(ResourceUtil.getKey("rabbitmq.uri"));
        connection = factory.newConnection();
        channel = connection.createChannel();

    }

    public void consumer(String exchangeName, final String queueName, String exchangeType, String bindKey) {
        try {
            init();
            channel.exchangeDeclare(exchangeName, exchangeType, false, false, null);
            channel.queueDeclare(queueName, false, false, false, null);
            channel.queueBind(queueName, exchangeName, bindKey);

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {
                    String msg = new String(body, "UTF-8");
                    System.out.println("queue:" + queueName + " Received message : '" + msg + "'");
                }
            };

            channel.basicConsume(queueName, true, consumer);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("consumer error");
        } finally {
        }
    }


    public static void main(String[] args) {
        MsgConsumer msgConsumer = new MsgConsumer();
        msgConsumer.consumer("LIZI_TEST_EXCHANGE11", "LIZI_TEST_QUEUE1", "direct", "lizi.test");
        msgConsumer.consumer("LIZI_TEST_EXCHANGE12", "LIZI_TEST_QUEUE2", "topic", "lizi.*");
        msgConsumer.consumer("LIZI_TEST_EXCHANGE13", "LIZI_TEST_QUEUE3", "fanout", "lizi.*");

    }
}
