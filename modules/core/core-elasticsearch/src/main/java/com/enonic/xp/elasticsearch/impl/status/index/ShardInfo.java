package com.enonic.xp.elasticsearch.impl.status.index;


import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ShardInfo
{
    final String id;

    final boolean primary;

    final String shardState;

    private ShardInfo( final Builder builder )
    {
        id = builder.id;
        primary = builder.primary;
        shardState = builder.shardState;
    }

    public ObjectNode toJson()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        if ( !StringUtils.isEmpty( id ) )
        {
            json.put( "id", id );
        }
        json.put( "primary", primary );
        if ( !StringUtils.isEmpty( shardState ) )
        {
            json.put( "state", shardState );
        }
        return json;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private String id;

        private boolean primary;

        private String shardState;

        private Builder()
        {
        }

        public Builder id( final String id )
        {
            this.id = id;
            return this;
        }

        public Builder primary( final boolean primary )
        {
            this.primary = primary;
            return this;
        }

        public Builder shardState( final String shardState )
        {
            this.shardState = shardState;
            return this;
        }

        public ShardInfo build()
        {
            return new ShardInfo( this );
        }
    }
}
