package com.enonic.xp.core.impl.hazelcast.status.objects;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

class QueueObjectReport
{
    private final String name;

    private final long size;

    private QueueObjectReport( Builder builder )
    {
        name = builder.name;
        size = builder.size;
    }

    ObjectNode toJson()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put( "name", name );
        json.put( "size", size );
        return json;
    }

    static Builder create()
    {
        return new Builder();
    }

    static class Builder
    {
        String name;

        int size;

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder size( final int size )
        {
            this.size = size;
            return this;
        }

        QueueObjectReport build()
        {
            return new QueueObjectReport( this );
        }
    }
}
