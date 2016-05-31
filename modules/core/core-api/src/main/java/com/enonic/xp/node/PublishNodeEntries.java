package com.enonic.xp.node;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.xp.support.AbstractImmutableEntitySet;

public class PublishNodeEntries
    extends AbstractImmutableEntitySet<PublishNodeEntry>
{

    private PublishNodeEntry entry;

    public PublishNodeEntries( final ImmutableSet<PublishNodeEntry> set )
    {
        super( set );
    }

    private PublishNodeEntries( final Builder builder )
    {
        super( ImmutableSet.copyOf( builder.entries ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Set<PublishNodeEntry> entries = Sets.newHashSet();

        private Builder()
        {
        }

        public Builder add( final PublishNodeEntry val )
        {
            this.entries.add( val );
            return this;
        }

        public Builder addAll( final Collection<PublishNodeEntry> values )
        {
            this.entries.addAll( values );
            return this;
        }


        public PublishNodeEntries build()
        {
            return new PublishNodeEntries( this );
        }
    }
}
