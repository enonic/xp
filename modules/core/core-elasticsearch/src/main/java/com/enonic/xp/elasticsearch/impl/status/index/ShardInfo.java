package com.enonic.xp.elasticsearch.impl.status.index;


import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

public class ShardInfo
{
    private final List<ShardDetails> unassigned;

    private final List<ShardDetails> relocating;

    private final List<ShardDetails> initializing;

    private final List<ShardDetails> started;

    private ShardInfo( Builder builder )
    {
        unassigned = builder.unassigned;
        relocating = builder.relocating;
        initializing = builder.initializing;
        started = builder.started;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public JsonNode toJson()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();

        json.set( "started", toJson( started ) );
        json.set( "unassigned", toJson( unassigned ) );
        json.set( "relocating", toJson( relocating ) );
        json.set( "initializing", toJson( initializing ) );

        return json;
    }

    private JsonNode toJson( final List<ShardDetails> shardInfoList )
    {

        final ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        shardInfoList.stream().
            map( ShardDetails::toJson ).
            forEach( arrayNode::add );

        return arrayNode;
    }

    public static final class Builder
    {
        private List<ShardDetails> unassigned = Lists.newArrayList();

        private List<ShardDetails> relocating = Lists.newArrayList();

        private List<ShardDetails> initializing = Lists.newArrayList();

        private List<ShardDetails> started = Lists.newArrayList();

        private Builder()
        {
        }

        public Builder unassigned( List<ShardDetails> unassigned )
        {
            this.unassigned = unassigned;
            return this;
        }

        public Builder relocating( List<ShardDetails> relocating )
        {
            this.relocating = relocating;
            return this;
        }

        public Builder initializing( List<ShardDetails> initializing )
        {
            this.initializing = initializing;
            return this;
        }

        public Builder started( List<ShardDetails> started )
        {
            this.started = started;
            return this;
        }

        public ShardInfo build()
        {
            return new ShardInfo( this );
        }
    }
}