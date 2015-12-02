package com.enonic.xp.elasticsearch.impl.status.index;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ShardSummary
{
    private final int started;

    private final int unassigned;

    private final int relocating;

    private final int initializing;

    private final int total;

    private ShardSummary( Builder builder )
    {
        this.started = builder.started;
        this.unassigned = builder.unassigned;
        this.relocating = builder.relocating;
        this.initializing = builder.initializing;
        this.total = this.initializing + this.relocating + this.started + this.unassigned;

    }

    public static Builder create()
    {
        return new Builder();
    }

    public ObjectNode toJson()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();

        json.put( "total", total );
        json.put( "started", started );
        json.put( "unassigned", unassigned );
        json.put( "relocating", relocating );
        json.put( "initializing", initializing );

        return json;
    }

    public static final class Builder
    {
        private int started;

        private int unassigned;

        private int relocating;

        private int initializing;

        private Builder()
        {
        }

        public Builder initializing( int initializing )
        {
            this.initializing = initializing;
            return this;
        }

        public Builder relocating( int relocating )
        {
            this.relocating = relocating;
            return this;
        }

        public Builder started( int started )
        {
            this.started = started;
            return this;
        }

        public Builder unassigned( int unassigned )
        {
            this.unassigned = unassigned;
            return this;
        }

        public ShardSummary build()
        {
            return new ShardSummary( this );
        }
    }
}
