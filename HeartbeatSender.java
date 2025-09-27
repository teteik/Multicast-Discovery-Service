import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

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
            this.sender.joinGroup(new InetSocketAddress(this.group, port), (NetworkInterface)null);
        } catch (IOException var5) {
            IOException e = var5;
            throw new RuntimeException(e);
        }
    }

    public void startHeartbeat() {
        while(true) {
            try {
                this.sendHeartbeat();
                Thread.sleep(3000L);
            } catch (InterruptedException var2) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    public void sendHeartbeat() {
        String HEARTBEAT = "HEARTBEAT";
        String msg = HEARTBEAT + " " + this.peerId;
        byte[] byteMsg = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(byteMsg, byteMsg.length, this.group, this.port);
        this.tryToSendPacket(packet);
    }

    private void tryToSendPacket(DatagramPacket packet) {
        try {
            this.sender.send(packet);
        } catch (IOException var3) {
            IOException e = var3;
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        this.sender.close();
    }
}
