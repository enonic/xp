package com.enonic.xp.elasticsearch.impl.status;

import org.elasticsearch.action.admin.cluster.node.info.NodeInfo;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoRequest;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoResponse;
import org.elasticsearch.client.Client;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.status.StatusReporter;

@Component(immediate = true)
public final class ClusterReporter
    implements StatusReporter
{
    private Client client;

    @Override
    public String getName()
    {
        return "cluster";
    }

    @Override
    public ObjectNode getReport()
    {
        final NodesInfoResponse info = getInfo();
        final ObjectNode json = JsonNodeFactory.instance.objectNode();

        json.put( "name", info.getClusterNameAsString() );
        final ArrayNode nodesJson = json.putArray( "nodes" );

        for ( final NodeInfo node : info.getNodes() )
        {
            nodesJson.add( toJson( node ) );
        }

        return json;
    }

    private ObjectNode toJson( final NodeInfo info )
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put( "hostName", info.getHostname() );
        return json;
    }

    private NodesInfoResponse getInfo()
    {
        final NodesInfoRequest req = new NodesInfoRequest().all();
        return this.client.admin().cluster().nodesInfo( req ).actionGet();
    }

    @Reference
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
