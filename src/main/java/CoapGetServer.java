import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.elements.util.NetworkInterfacesUtil;

/**
 * @author ebruno
 */
public class CoapGetServer extends CoapServer {
    private static final int COAP_PORT = 
            Configuration.getStandard().get( CoapConfig.COAP_PORT );

    public static void main(String[] args) {
        try {
            CoapGetServer server = new CoapGetServer();
            server.start();
        }
        catch ( Exception e ) {
            System.err.println("Failed to initialize server: " + e.getMessage());
        }
    }

    boolean addMulticast = false;
    /**
     * Add individual endpoints listening on default CoAP port on all IPv4
     * addresses of all network interfaces.
     */
    private void addEndpoints() {
        Configuration config = Configuration.getStandard();
        for (InetAddress addr : NetworkInterfacesUtil.getNetworkInterfaces()) {
            InetSocketAddress bindToAddress = new InetSocketAddress(addr, COAP_PORT);

            CoapEndpoint.Builder builder = new CoapEndpoint.Builder();
            builder.setInetSocketAddress(bindToAddress);
            builder.setConfiguration(config);
            addEndpoint(builder.build());
        }

        
        if ( addMulticast ) {
            try {
                // Add a multicast address listener
                InetSocketAddress mcAddress = new InetSocketAddress("224.0.1.187", COAP_PORT);
                CoapEndpoint.Builder builder = new CoapEndpoint.Builder();
                builder.setInetSocketAddress(mcAddress);
                builder.setConfiguration(config);
                addEndpoint(builder.build());

                /*
                InetAddress addr = InetAddress.getByName("224.0.1.187");
                boolean mc = addr.isMulticastAddress();
                mc = addr.isMCGlobal();

                //bindToAddress = new InetSocketAddress("224.0.1.187", COAP_PORT);
                bindToAddress = new InetSocketAddress(addr, COAP_PORT);

                multicast = new CoapEndpoint(bindToAddress);
                addEndpoint(multicast);
                */
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }
//        InetSocketAddress bindToAddress;
//        CoapEndpoint multicast;
//        bindToAddress = new InetSocketAddress("localhost", COAP_PORT);
//        multicast = new CoapEndpoint(bindToAddress);
//        addEndpoint(multicast);
//
//        bindToAddress = new InetSocketAddress("FF05:::FD", COAP_PORT);
//        CoapEndpoint multicast2 = new CoapEndpoint(bindToAddress);
//        addEndpoint(multicast2);
        }
    }

    /*
     * Constructor for a new Hello-World server. Here, the resources
     * of the server are initialized.
     */
    public CoapGetServer() throws SocketException {
        //super(ports);
        super();

        // add endpoints on all IP addresses
        addEndpoints();

        // provide an instance of a Hello-World resource
        add(new HelloWorldResource());
        add(new GoodbyeResource());
    }

    /*
     * Definition of the Hello-World Resource
     */
    class HelloWorldResource extends CoapResource {
        public HelloWorldResource() {
            super("helloWorld"); // set resource identifier
            getAttributes().setTitle("Hello-World Resource");
        }

        @Override
        public void handleGET(CoapExchange exchange) {
            exchange.respond("Hello, World!");
        }
    }
    
    class GoodbyeResource extends CoapResource {
        public GoodbyeResource() {
            super("bye"); // set resource identifier
            getAttributes().setTitle("Goodbye Resource");
        }

        @Override
        public void handleGET(CoapExchange exchange) {
            exchange.respond("So long!");
        }
    }
}
