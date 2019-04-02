package controllers;

import com.google.common.collect.ImmutableList;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.nodes.VariableNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.enumerated.IdType;


import java.util.Collections;
import java.util.List;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class WriteExample implements ClientInterface{
public static void main(String[] args) throws Exception {
        WriteExample example = new WriteExample();

        new ClientExampleRunner(example).run();
    }

 @Override
    public void run(OpcUaClient client, CompletableFuture<OpcUaClient> future) throws Exception {
     Config conf = ConfigFactory.load();
        client.connect().get();
            List<NodeId> nodeIds = ImmutableList.of(new NodeId(conf.getInt("write.ns"), conf.getString("write.node")));
 Thread.sleep(conf.getInt("write.interval"));
 
   Random r =  new Random();

           //************FOR Double Value*****************************//
           double random = r.nextDouble();
           DataValue dv = new DataValue(new Variant(random), null, null);

           //***********************************************************//


            //************FOR Float Value*****************************//
           /*float e = r.nextFloat();

           DataValue dv = new DataValue(new Variant(e), null, null);*/

            //***********************************************************//
            
            CompletableFuture<List<StatusCode>> f = client.writeValues(nodeIds, ImmutableList.of(dv));
            
             List<StatusCode> statusCodes = f.get();
            StatusCode status = statusCodes.get(0);
            
                System.out.println("Wrote --" + dv + "  to nodeId=" + nodeIds.get(0) + "status:" + status);
            future.complete(client);
            
      }
    }
}
