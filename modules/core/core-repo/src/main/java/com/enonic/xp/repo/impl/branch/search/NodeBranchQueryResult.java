package com.enonic.xp.repo.impl.branch.search;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.enonic.xp.repo.impl.branch.storage.BranchNodeVersion;
import com.enonic.xp.support.AbstractImmutableEntityList;

public class NodeBranchQueryResult
    extends AbstractImmutableEntityList<BranchNodeVersion>
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
        private final List<BranchNodeVersion> entries = Lists.newLinkedList();


        public Builder add( final BranchNodeVersion branchNodeVersion )
        {
            this.entries.add( branchNodeVersion );
            return this;
        }

        public NodeBranchQueryResult build()
        {
            return new NodeBranchQueryResult( this );
        }
    }

}
