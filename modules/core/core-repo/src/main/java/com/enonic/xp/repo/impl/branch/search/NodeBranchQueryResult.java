package com.enonic.xp.repo.impl.branch.search;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.support.AbstractImmutableEntityList;

public class NodeBranchQueryResult
    extends AbstractImmutableEntityList<NodeBranchEntry>
{
    private NodeBranchQueryResult( final Builder builder )
    {
        super( ImmutableList.copyOf( builder.entries ) );
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
        private final List<NodeBranchEntry> entries = new ArrayList<>();


        public Builder add( final NodeBranchEntry nodeBranchEntry )
        {
            this.entries.add( nodeBranchEntry );
            return this;
        }

        public NodeBranchQueryResult build()
        {
            return new NodeBranchQueryResult( this );
        }
    }

}
