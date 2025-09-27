import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class HeartbeatReceiver {
    private final MulticastSocket receiver;
    private final PeerRegistry peerRegistry;

    HeartbeatReceiver(String host, int port, PeerRegistry peerRegistry) {
        try {
            InetAddress group = InetAddress.getByName(host);
            this.receiver = new MulticastSocket(port);
            receiver.joinGroup(group);
            this.peerRegistry = peerRegistry;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void listenForHeartbeats() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                DatagramPacket packet = receiveHeartbeatPacket();
                processHeartbeatPacket(packet);
            } catch (IOException ignored) {
            }
        }
    }

    private DatagramPacket receiveHeartbeatPacket() throws IOException {
        byte[] buf = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        receiver.receive(packet);
        return packet;
    }

    private void processHeartbeatPacket(DatagramPacket packet) {
        String msg = new String(packet.getData(), 0, packet.getLength()).trim();
        if (msg.startsWith("HEARTBEAT ")) {
            String peerId = msg.substring("HEARTBEAT ".length()).trim();
            InetAddress senderAddress = packet.getAddress();
            peerRegistry.updatePeer(peerId, senderAddress);
        }
    }

    public void stop() {
        receiver.close();
    }
}
