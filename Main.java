public class Main {
    public static void main(String[] args) {
        MulticastDiscoveryService service = new MulticastDiscoveryService();
        service.runMulticast(args[0]);
    }
}
