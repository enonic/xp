package com.enonic.xp.repo.impl.branch.search;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.support.AbstractImmutableEntitySet;

public class NodeBranchQueryResult
    extends AbstractImmutableEntitySet<NodeBranchQueryResultEntry>
{
    private NodeBranchQueryResult( final Builder builder )
    {
        super( ImmutableSet.copyOf( builder.entries ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static NodeBranchQueryResult empty()
    {
        return new Builder().build();
    }

    public static class Builder
    {
        private final Set<NodeBranchQueryResultEntry> entries = Sets.newLinkedHashSet();


        public Builder add( final String nodeId, final String nodeVersionId )
        {
            this.entries.add( NodeBranchQueryResultEntry.create().
                nodeId( NodeId.from( nodeId ) ).
                nodeVersionId( NodeVersionId.from( nodeVersionId ) ).
                build() );

            return this;
        }

        public NodeBranchQueryResult build()
        {
            return new NodeBranchQueryResult( this );
        }
    }

}
