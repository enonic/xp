package com.enonic.xp.elasticsearch.impl.status.index;


import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ShardInfo
{
    final String index;

    final int id;

    final boolean primary;

    final String state;

    final String node;

    private ShardInfo( final Builder builder )
    {
        index = builder.index;
        id = builder.id;
        primary = builder.primary;
        state = builder.state;
        node = builder.node;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ObjectNode toJson()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();

        final ObjectNode shardId = JsonNodeFactory.instance.objectNode();
        if ( !StringUtils.isEmpty( index ) )
        {
            shardId.put( "index", index );
        }
        shardId.put( "id", this.id );
        json.set( "id", shardId );

        json.put( "primary", primary );
        if ( !StringUtils.isEmpty( state ) )
        {
            json.put( "state", state );
        }
        if ( !StringUtils.isEmpty( node ) )
        {
            json.put( "node", node );
        }
        return json;
    }


    public static final class Builder
    {
        private String index;

        private int id;

        private boolean primary;

        private String state;

        private String node;

        private Builder()
        {
        }

        public Builder index( final String index )
        {
            this.index = index;
            return this;
        }

        public Builder id( final int id )
        {
            this.id = id;
            return this;
        }

        public Builder primary( final boolean primary )
        {
            this.primary = primary;
            return this;
        }

        public Builder state( final String state )
        {
            this.state = state;
            return this;
        }

        public Builder node( final String node )
        {
            this.node = node;
            return this;
        }

        public ShardInfo build()
        {
            return new ShardInfo( this );
        }
    }
}
