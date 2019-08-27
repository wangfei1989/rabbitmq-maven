package com.gper.lizi.rabbitmq;

import com.gper.lizi.util.ResourceUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.UUID;

/**
 * lizi
 * 2019/8/27
 */
public class MsgProducer {


    private ConnectionFactory factory;

    private Connection connection;

    private Channel channel;


    private void init() throws Exception{
        factory = new ConnectionFactory();
        factory.setUri(ResourceUtil.getKey("rabbitmq.uri"));
        connection = factory.newConnection();
        channel = connection.createChannel();

    }

    public void produce(String exchangeName,String exchangeType,String routKey,String message){
        try {
            init();

            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                    .deliveryMode(2)   // 2代表持久化
                    .contentEncoding("UTF-8")  // 编码
                    .expiration("30000")  // TTL，过期时间
                    .messageId(String.valueOf(UUID.randomUUID()))
                    .build();

            channel.exchangeDeclare(exchangeName,exchangeType,false,false,null);
            channel.basicPublish(exchangeName, routKey, properties, message.getBytes());

        }catch (Exception e){
            System.out.println("produce message error");
        }finally {
            close();
        }
    }

    private void close(){
        try {
            if(null != channel){
                channel.close();
            }
            if (null != connection) {
                connection.close();
            }
        }catch (Exception e){
            System.out.println("connection close fail");
        }finally {
            channel = null;
            connection = null;
        }
    }

    public static void main(String[] args) {

        MsgProducer producer = new MsgProducer();
        String message = "hello rabbitMQ, push message,count:";

        for(int i = 0;i<10;i++){
            if(i < 5) {
                producer.produce("LIZI_TEST_EXCHANGE11", "direct","lizi.test",message + i);
            }else if(i >= 5 && i< 8){
                producer.produce("LIZI_TEST_EXCHANGE12","topic","lizi.test2",message + i);
            }else{
                producer.produce("LIZI_TEST_EXCHANGE13","fanout","",message + i);
            }
        }
        try {
            Thread.sleep(3000);
        }catch (Exception e){

        }

    }


}
