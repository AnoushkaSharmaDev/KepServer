package controllers;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.api.identity.UsernameProvider;
import org.eclipse.milo.opcua.stack.client.UaTcpStackClient;
import org.eclipse.milo.opcua.stack.core.Stack;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.eclipse.milo.opcua.stack.core.util.CryptoRestrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.File;
import java.security.Security;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ClientExampleRunner {
    static {
        CryptoRestrictions.remove();

        // Required for SecurityPolicy.Aes256_Sha256_RsaPss
        Security.addProvider(new BouncyCastleProvider());
    }
    
    private final CompletableFuture<OpcUaClient> future = new CompletableFuture<>();
  private final ClientExample clientExample;
    private final boolean serverRequired;

    public ClientExampleRunner(ClientExample clientExample) throws Exception {
        this(clientExample, true);
    }
       public ClientExampleRunner(ClientExample clientExample, boolean serverRequired) throws Exception {
        this.clientExample = clientExample;
        this.serverRequired = serverRequired;

    }
    
        private OpcUaClient createClient() throws Exception {
        Config conf = ConfigFactory.load();
        File securityTempDir = new File(System.getProperty("java.io.tmpdir"), "security");
        if (!securityTempDir.exists() && !securityTempDir.mkdirs()) {
            throw new Exception("unable to create security dir: " + securityTempDir);
        }
        
        SecurityPolicy securityPolicy = clientExample.getSecurityPolicy();

        EndpointDescription[] endpoints;

        try {
            endpoints = UaTcpStackClient
                    .getEndpoints(clientExample.getEndpointUrl())
                    .get();
        } catch (Throwable ex) {
            // try the explicit discovery endpoint as well
            String discoveryUrl = clientExample.getEndpointUrl() + "/discovery";
            System.out.println("Trying explicit discovery URL: "+ discoveryUrl);
            endpoints = UaTcpStackClient
                    .getEndpoints(discoveryUrl)
                    .get();
        }

        EndpointDescription endpoint = Arrays.stream(endpoints)
                .filter(e -> e.getSecurityPolicyUri().equals(securityPolicy.getSecurityPolicyUri()))
                .findFirst().orElseThrow(() -> new Exception("no desired endpoints returned"));

        System.out.println("Using endpoint: "+ endpoint.getEndpointUrl()+ " with security: "+securityPolicy);

        OpcUaClientConfig config = OpcUaClientConfig.builder()
                .setApplicationName(LocalizedText.english("eclipse milo opc-ua client"))
                .setApplicationUri("urn:eclipse:milo:examples:client")
               .setEndpoint(endpoint)
                .setIdentityProvider(clientExample.getIdentityProvider())
               /* .setRequestTimeout(uint(5000))*/
               /* .setIdentityProvider(new UsernameProvider("Administrator", "Abcd123"))  //uncomment if kep server is secured with username and password.
                .build();

        return new OpcUaClient(config);
    }

        public void run() {
        try {
            OpcUaClient client = createClient();
            try {
                clientExample.run(client, future);
                future.get(15, TimeUnit.SECONDS);
            } catch (Throwable t) {
                System.out.println("Error running client example: {}"+ t.getMessage()+ t);
                future.completeExceptionally(t);
            }
        } catch (Throwable t) {
            System.out.println("Error getting client: {}"+ t.getMessage()+ t);

            future.completeExceptionally(t);

            try {
                Thread.sleep(1000);
             //   System.exit(0);   //uncomment if you want the connection only for one time.
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            Thread.sleep(999999999);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    
}
