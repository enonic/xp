package com.enonic.xp.core.impl.hazelcast.status.objects;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class FencedLockObjectReport
{
    private final String name;

    private final boolean locked;

    private final int lockCount;


    private FencedLockObjectReport( Builder builder )
    {
        name = builder.name;
        locked = builder.locked;
        lockCount = builder.lockCount;
    }

    ObjectNode toJson()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put( "name", name );
        json.put( "locked", locked );
        json.put( "lockCount", lockCount );
        return json;
    }

    static Builder create()
    {
        return new Builder();
    }

    static class Builder
    {
        String name;

        boolean locked;

        int lockCount;

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder locked( final boolean locked )
        {
            this.locked = locked;
            return this;
        }

        public Builder lockCount( final int lockCount )
        {
            this.lockCount = lockCount;
            return this;
        }

        FencedLockObjectReport build()
        {
            return new FencedLockObjectReport( this );
        }
    }
}
