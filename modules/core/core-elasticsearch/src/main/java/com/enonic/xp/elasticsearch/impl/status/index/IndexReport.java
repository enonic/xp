package com.enonic.xp.elasticsearch.impl.status.index;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class IndexReport
{
    List<ShardInfo> shardInfos;

    String errorMessage;

    private IndexReport( final Builder builder )
    {
        shardInfos = builder.shardInfos;
        errorMessage = builder.errorMessage;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ObjectNode toJson()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        if ( shardInfos != null && !shardInfos.isEmpty() )
        {
            json.set( "shards", toJson( shardInfos ) );
        }
        if ( StringUtils.isNotEmpty( errorMessage ) )
        {
            json.put( "errorMessage", errorMessage );
        }
        return json;
    }

    private JsonNode toJson( List<ShardInfo> shardInfos )
    {
        final ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        shardInfos.stream().
            map( ShardInfo::toJson ).
            forEach( arrayNode::add );

        return arrayNode;
    }


    public static final class Builder
    {
        private List<ShardInfo> shardInfos = new LinkedList<>();

        private String errorMessage;

        private Builder()
        {
        }

        public Builder addShardInfo( final ShardInfo shardInfo )
        {
            this.shardInfos.add( shardInfo );
            return this;
        }

        public Builder errorMessage( final String errorMessage )
        {
            this.errorMessage = errorMessage;
            return this;
        }

        public IndexReport build()
        {
            return new IndexReport( this );
        }
    }
}
