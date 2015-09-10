package com.enonic.wem.repo.internal.storage.branch;

import java.util.Collection;
import java.util.LinkedHashSet;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.xp.support.AbstractImmutableEntitySet;

public class NodeBranchVersions
    extends AbstractImmutableEntitySet<NodeBranchVersion>
{
    private NodeBranchVersions( final ImmutableSet<NodeBranchVersion> set )
    {
        super( set );
    }

    public static NodeBranchVersions from( final NodeBranchVersion... nodeBranchVersions )
    {
        return new NodeBranchVersions( ImmutableSet.copyOf( nodeBranchVersions ) );
    }

    public static NodeBranchVersions from( final Collection<NodeBranchVersion> nodeBranchVersions )
    {
        return new NodeBranchVersions( ImmutableSet.copyOf( nodeBranchVersions ) );
    }

    private NodeBranchVersions( final Builder builder )
    {
        super( ImmutableSet.copyOf( builder.branchVersions ) );
    }

    public static NodeBranchVersions empty()
    {
        ImmutableSet<NodeBranchVersion> empty = ImmutableSet.of();
        return new NodeBranchVersions( empty );
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static class Builder
    {
        private final LinkedHashSet<NodeBranchVersion> branchVersions = Sets.newLinkedHashSet();

        public Builder add( final NodeBranchVersion nodeBranchVersion )
        {
            this.branchVersions.add( nodeBranchVersion );
            return this;
        }

        public NodeBranchVersions build()
        {
            return new NodeBranchVersions( this );
        }

    }

}
