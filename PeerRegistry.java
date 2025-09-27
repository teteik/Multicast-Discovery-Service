import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PeerRegistry {
    private final Map<String, LivePeer> peers = new ConcurrentHashMap<>();
    private final long timeout;

    PeerRegistry(long timeout) {
        this.timeout = timeout;
    }

    public void updatePeer(String peerId, InetAddress address) {
        LivePeer updated = new LivePeer(peerId, address, System.currentTimeMillis());
        boolean isNew = !peers.containsKey(peerId);
        peers.put(peerId, updated);

        if(isNew) {
            printLivePeers();
        }
    }

    public void cleanPeers() {
        long now = System.currentTimeMillis();
        int before = peers.size();
        peers.entrySet().removeIf(entry -> now - entry.getValue().getLastHeartbeat() > timeout);
        if(peers.size() != before) {
            printLivePeers();
        }
    }

    private void printLivePeers() {
        System.out.println("=== Live Peers ===");
        List<LivePeer> livePeers = getLivePeers();
        if (livePeers.isEmpty()) {
            System.out.println("No live peers.");
        } else {
            for (LivePeer peer : livePeers) {
                System.out.println(peer.getId() + " @ " + peer.getAddress().getHostAddress());
            }
        }
        System.out.println("==================\n");
    }

    public List<LivePeer> getLivePeers() {
        return new ArrayList<>(peers.values());
    }
}
