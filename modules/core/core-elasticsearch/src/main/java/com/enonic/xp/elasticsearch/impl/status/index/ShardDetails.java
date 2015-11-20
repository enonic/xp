package com.enonic.xp.elasticsearch.impl.status.index;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ShardDetails
{
    private final String id;

    private final String nodeAddress;

    private final String nodeId;

    private final boolean primary;

    private final String relocatingNode;

    private ShardDetails( Builder builder )
    {
        id = builder.id;
        nodeId = builder.nodeId;
        primary = builder.primary;
        this.nodeAddress = builder.nodeAddress;
        relocatingNode = builder.relocatingNode;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ObjectNode toJson()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();

        json.put( "id", id );

        if ( nodeId != null )
        {
            json.put( "nodeId", nodeId );
        }

        if ( nodeAddress != null )
        {
            json.put( "nodeAddress", nodeAddress );
        }

        json.put( "type", primary ? "PRIMARY" : "REPLICA" );

        if ( relocatingNode != null )
        {
            json.put( "relocatingTo", relocatingNode );
        }

        return json;
    }

    public static final class Builder
    {
        private String id;

        private String nodeId;

        private boolean primary;

        private String relocatingNode;

        private String nodeAddress;

        private Builder()
        {
        }

        public Builder id( String id )
        {
            this.id = id;
            return this;
        }

        public Builder nodeId( String node )
        {
            this.nodeId = node;
            return this;
        }

        public Builder nodeAddress( final String nodeAddress )
        {
            this.nodeAddress = nodeAddress;
            return this;
        }

        public Builder relocatingNode( String relocatingNode )
        {
            this.relocatingNode = relocatingNode;
            return this;
        }

        public Builder primary( boolean primary )
        {
            this.primary = primary;
            return this;
        }

        public ShardDetails build()
        {
            return new ShardDetails( this );
        }
    }
}
