import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

    /**
     * general JMS client with one producer and one consumer
     *
     * @author Sandro Leuchter
     *
     */
    public class JMSClient implements MessageListener {
        private Connection connection;
        private Session session;
        private MessageProducer producer;
        private MessageConsumer consumer;

        /**
         * constructor, establishes and starts connection to JMS provider specified in
         * JNDI (via jndi.properties), afterwards producer and consumer are ready
         *
         * @param sendDest    Destination for producer
         * @param receiveDest Destination for consumer
         *
         * @throws NamingException JNDI exceptions
         * @throws JMSException    JMS exceptions
         * @see javax.jms.Destination
         */
        public JMSClient(String sendDest, String receiveDest) throws NamingException, JMSException {
            Context ctx = new InitialContext();
            ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
            this.connection = factory.createConnection();
            this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destOut = (Destination) ctx.lookup(sendDest);
            Destination destIn = (Destination) ctx.lookup(receiveDest);
            this.producer = this.session.createProducer(destOut);
            this.consumer = this.session.createConsumer(destIn);
            this.consumer.setMessageListener(this);
            this.connection.start();
        }

        /**
         * asynchronous message consumption
         *
         * @see javax.jms.MessageListener
         */
        @Override
        public void onMessage(Message message) {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                try {
                    System.out.println(textMessage.getText());
                } catch (JMSException e) {
                    System.err.println(e);
                }
            }
        }

        /**
         * main routine and starting point of program
         *
         * @param args not used
         */
        public static void main(String[] args) {
            JMSClient node = null;
            try {
                node = new JMSClient("var.mom.jms.client.queue1", "var.mom.jms.client.queue2");
                BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
                String line;
                while (true) {
                    line = input.readLine();
                    SyslogMessage message = new SyslogMessage(SyslogMessage.Facility.PRINTER, SyslogMessage.Severity.CRITICAL, new AsciiChars.L255("host"),new AsciiChars.L048("testapp"),new AsciiChars.L128("007"),new AsciiChars.L032("008"),null,new SyslogMessage.TextMessage(line));
                    node.producer.send(node.session.createObjectMessage(message));
                }
            } catch (NamingException | JMSException | IOException e) {
                System.err.println(e);
            } finally {
                try {
                    if ((node != null) && (node.producer != null)) {
                        node.producer.close();
                    }
                    if ((node != null) && (node.consumer != null)) {
                        node.consumer.close();
                    }
                    if ((node != null) && (node.session != null)) {
                        node.session.close();
                    }
                    if ((node != null) && (node.connection != null)) {
                        node.connection.close();
                    }
                } catch (JMSException e) {
                    System.err.println(e);
                }
            }
        }
    }

