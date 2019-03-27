package com.enonic.xp.node;

import java.util.Collection;
import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.xp.support.AbstractImmutableEntitySet;

@Beta
public class RoutableNodeVersionIds
    extends AbstractImmutableEntitySet<RoutableNodeVersionId>
{
    private RoutableNodeVersionIds( final ImmutableSet<RoutableNodeVersionId> set )
    {
        super( set );
    }

    public static RoutableNodeVersionIds empty()
    {
        return new RoutableNodeVersionIds( ImmutableSet.<RoutableNodeVersionId>of() );
    }

    public static RoutableNodeVersionIds from( final RoutableNodeVersionId... routableNodeVersionIds )
    {
        return new RoutableNodeVersionIds( ImmutableSet.copyOf( routableNodeVersionIds ) );
    }


    public static RoutableNodeVersionIds from( final Collection<RoutableNodeVersionId> routableNodeVersionIds )
    {
        return new RoutableNodeVersionIds( ImmutableSet.copyOf( routableNodeVersionIds ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        final Set<RoutableNodeVersionId> routableNodeVersionIds = Sets.newLinkedHashSet();

        public Builder add( final RoutableNodeVersionId routableNodeVersionId )
        {
            this.routableNodeVersionIds.add( routableNodeVersionId );
            return this;
        }

        public Builder addAll( final Collection<RoutableNodeVersionId> routableNodeVersionIds )
        {
            this.routableNodeVersionIds.addAll( routableNodeVersionIds );
            return this;
        }

        public RoutableNodeVersionIds build()
        {
            return new RoutableNodeVersionIds( ImmutableSet.copyOf( this.routableNodeVersionIds ) );
        }

    }

}
