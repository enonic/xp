package com.enonic.xp.elasticsearch.impl.status.index;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ShardDetails
{
    private final String id;

    private final String node;

    private final boolean primary;

    private final String relocatingNode;

    private ShardDetails( Builder builder )
    {
        id = builder.id;
        node = builder.node;
        primary = builder.primary;
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

        if ( node != null )
        {
            json.put( "node", node );
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

        private String node;

        private boolean primary;

        private String relocatingNode;

        private Builder()
        {
        }

        public Builder id( String id )
        {
            this.id = id;
            return this;
        }

        public Builder node( String node )
        {
            this.node = node;
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
