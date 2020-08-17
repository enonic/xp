package com.enonic.xp.core.impl.hazelcast.status.objects;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

class TopicObjectReport
{
    private final String name;


    private TopicObjectReport( Builder builder )
    {
        name = builder.name;
    }

    ObjectNode toJson()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put( "name", name );
        return json;
    }

    static Builder create()
    {
        return new Builder();
    }

    static class Builder
    {
        String name;

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        TopicObjectReport build()
        {
            return new TopicObjectReport( this );
        }
    }
}
