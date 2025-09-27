import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class HeartbeatSender {
    private final MulticastSocket sender;
    private InetAddress group = null;
    private final int port;
    private final String peerId;

    HeartbeatSender(String host, int port, String peerId) {
        try {
            this.peerId = peerId;
            this.group = InetAddress.getByName(host);
            this.port = port;
            this.sender = new MulticastSocket(port);
            sender.joinGroup(group);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void startHeartbeat() {
        while(true) {
            try {
                sendHeartbeat();
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void sendHeartbeat()  {
        String HEARTBEAT = "HEARTBEAT";
        String msg = HEARTBEAT + " " + peerId;
        byte[] byteMsg = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(byteMsg, byteMsg.length, group, port);
        tryToSendPacket(packet);
    }

    private void tryToSendPacket(DatagramPacket packet) {
        try {
            sender.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        sender.close();
    }
}
