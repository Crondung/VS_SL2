import javax.jms.JMSException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.sql.Timestamp;
import java.util.Date;

public class Adapter {
    private static int SYSLOG_PORT = 514;

    JMSProducer jmsProducer;

    Adapter() throws JMSException {
        System.out.println("Starting Server");
        Thread adapterThread = new Thread(AdapterService);
        adapterThread.start();
        System.out.println("Server successfully started");

        this.jmsProducer = new JMSProducer("127.0.0.1", "test");
    }

    Runnable AdapterService = () -> {
        System.out.println("Adapter running in Thread: " + Thread.currentThread().getName());
        try {
            DatagramSocket socket = new DatagramSocket(SYSLOG_PORT);
            while (true) {
                DatagramPacket in = new DatagramPacket(new byte[1024], 1024);
                socket.receive(in);
                String received = new String(
                        in.getData(), 0, in.getLength());
                if (received.equals("")) {
                    DatagramPacket out = new DatagramPacket(new byte[0], 0, in.getAddress(), in.getPort());
                    socket.send(out);
                    //sending a response when getting an empty request, possibly from a broadcasted request, can also be used for autodiscovery
                } else {
                    Date date = new Date();
                    Timestamp timestamp = new Timestamp(date.getTime());

                    //forward to JMS or MQTT Broker
                    this.jmsProducer.sendMessage(received);
                    System.out.println("" + timestamp + " | Sender: " + in.getAddress() + ": " + received);
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (IOException | JMSException e) {
            throw new RuntimeException(e);
        }

    };

    public static void main(String[] args) throws JMSException {
        new Adapter();

    }
}
