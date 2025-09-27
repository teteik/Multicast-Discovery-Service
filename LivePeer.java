import java.net.InetAddress;

public class LivePeer {
    private final String id;
    private final InetAddress address;
    private final long lastHeartbeat;

    LivePeer(String id, InetAddress address, long lastHeartbeat) {
        this.address = address;
        this.id = id;
        this.lastHeartbeat = lastHeartbeat;
    }

    public String getId() {
        return id;
    }

    public InetAddress getAddress() {
        return address;
    }
    public long getLastHeartbeat() {
        return lastHeartbeat;
    }
}
