package com.enonic.xp.node;

import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.xp.support.AbstractImmutableEntitySet;

@Beta
public class NodeVersionIds
    extends AbstractImmutableEntitySet<NodeVersionId>
{
    private NodeVersionIds( final ImmutableSet<NodeVersionId> set )
    {
        super( set );
    }

    public static NodeVersionIds empty()
    {
        return new NodeVersionIds( ImmutableSet.<NodeVersionId>of() );
    }

    public static NodeVersionIds from( final NodeVersionId... nodeVersionIds )
    {
        return new NodeVersionIds( ImmutableSet.copyOf( nodeVersionIds ) );
    }


    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        final Set<NodeVersionId> nodeVersionIds = Sets.newLinkedHashSet();

        public Builder add( final NodeVersionId nodeVersionId )
        {
            this.nodeVersionIds.add( nodeVersionId );
            return this;
        }


        public NodeVersionIds build()
        {
            return new NodeVersionIds( ImmutableSet.copyOf( this.nodeVersionIds ) );
        }

    }

}
