package controllers;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.sdk.client.api.identity.IdentityProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;

import java.util.concurrent.CompletableFuture;

public interface ClientInterface {
    default String getEndpointUrl() {
       //provide the end point url of sever.
        
       return "opc.tcp://DESKTOP-OPK:49320";
    }

    default SecurityPolicy getSecurityPolicy() {
        return SecurityPolicy.None;
      //  return SecurityPolicy.Basic128Rsa15;
    }


    default IdentityProvider getIdentityProvider() {
        return new AnonymousProvider();
    }

    void run(OpcUaClient client, CompletableFuture<OpcUaClient> future) throws Exception;
}
