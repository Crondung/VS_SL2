import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class JMSConsumer {

    private QueueConnectionFactory jmsConnFactory;
    private QueueConnection jmsConnection;
    private QueueSession jmsSession;
    private Queue jmsQueue;
    private QueueReceiver jmsReceiver;

    private MessageProducer messageProducer;

    JMSConsumer(String brokerURL, String topic) throws JMSException {
        initJMSConnection(brokerURL, topic);
    }

    private void initJMSConnection(String brokerURL, String topic) throws JMSException {
        this.jmsConnFactory = new ActiveMQConnectionFactory("tcp://" + brokerURL);
        this.jmsConnection = this.jmsConnFactory.createQueueConnection();
        this.jmsSession = this.jmsConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        this.jmsQueue = this.jmsSession.createQueue(topic);
        this.messageProducer = this.jmsSession.createProducer(this.jmsQueue);
    }

    public void sendMessage(String messageText) throws JMSException {
        System.out.println("JMSProducer: " + messageText);
        Message message = this.jmsSession.createTextMessage(messageText);
        this.messageProducer.send(message);
    }

}
