package controllers;
import com.avaje.ebean.Ebean;
import com.google.common.collect.ImmutableList;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import com.typesafe.config.ConfigObject;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.nodes.VariableNode;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.ServerState;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;


import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class Read implements ClientInterface{

public static void main(String[] args) throws Exception {
        ReadExample example = new ReadExample();

        new ClientExampleRunner(example).run();
    }

 @Override
    public void run(OpcUaClient client, CompletableFuture<OpcUaClient> future) throws Exception {
     Config conf = ConfigFactory.load();
while(true){
Thread.sleep(9000);
 ConfigObject NodeList = conf.getObject("node.list");
String[] b = NodeList.get( "node").toString().replaceAll("SimpleConfigList\\(","").replaceAll("[\"]", "").
                replaceAll("\\)", "").replaceAll("[\\[\\]]", "").replaceAll("\"", "'").split(",");
 for(String v:b){
                String nodeAddr =v;
                System.out.println("node"+nodeAddr);
                Date sysDate = new Date();
                // synchronous connect
                  client.connect().get();

                    // synchronous read request via VariableNode
                  
                    VariableNode node = client.getAddressSpace().createVariableNode(NodeId.parse(nodeAddr));
                   DataValue value = node.readValue().get();
                System.out.println("Node Value: "+value.getValue().toString()+"for Node : "+nodeAddr);
                   

                    readServerStateAndTime(client).thenAccept(values -> {
                        DataValue v0 = values.get(0);
                        DataValue v1 = values.get(1);

                      System.out.println("State={}"+ ServerState.from((Integer) v0.getValue().getValue()));
                        System.out.println("CurrentTime={}"+v1.getValue().getValue());

                        future.complete(client);
                    });
                }

              }
   
    }
        private CompletableFuture<List<DataValue>> readServerStateAndTime(OpcUaClient client) {
        List<NodeId> nodeIds = ImmutableList.of(
                Identifiers.Server_ServerStatus_State,
                Identifiers.Server_ServerStatus_CurrentTime);

        return client.readValues(0.0, TimestampsToReturn.Both, nodeIds);
    }

}
