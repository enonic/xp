package com.enonic.xp.node;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

public class NodeVersionsQuery
    extends AbstractQuery
{
    private final ImmutableSet<String> ids;

    private NodeVersionsQuery( final Builder builder )
    {
        super( builder );
        this.ids = builder.ids.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Set<String> getIds()
    {
        return ids;
    }

    public static final class Builder
        extends AbstractQuery.Builder<Builder>
    {
        private ImmutableSet.Builder<String> ids = ImmutableSet.builder();

        private Builder()
        {
        }

        public Builder addId( String id )
        {
            this.ids.add( id );
            return this;
        }

        public Builder addIds( Collection<String> ids )
        {
            this.ids.addAll( ids );
            return this;
        }

        public NodeVersionsQuery build()
        {
            return new NodeVersionsQuery( this );
        }
    }
}
