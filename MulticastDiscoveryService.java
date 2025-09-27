import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MulticastDiscoveryService {


    public void runMulticast(String peerId) {
        String host = "ff02::1";
        //String host = "224.0.0.3";
        int port = 5500;
        long timeout = 5000;

        PeerRegistry peerRegistry = new PeerRegistry(timeout);

        HeartbeatSender sender = new HeartbeatSender(host, port, peerId);
        HeartbeatReceiver receiver = new HeartbeatReceiver(host, port, peerRegistry);

        Thread senderThread = new Thread(sender::startHeartbeat);
        senderThread.start();
        Thread receiverThread = new Thread(receiver::listenForHeartbeats);
        receiverThread.start();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(peerRegistry::cleanPeers, 5, 5, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            sender.stop();
            receiver.stop();
            scheduler.shutdown();
            senderThread.interrupt();
            receiverThread.interrupt();
            try {
                senderThread.join();
                receiverThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }));
    }
}
