package com.enonic.wem.repo.internal.storage.branch;

import java.util.Collection;
import java.util.LinkedHashSet;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.xp.support.AbstractImmutableEntitySet;

public class NodeBranchVersions
    extends AbstractImmutableEntitySet<BranchNodeVersion>
{
    private NodeBranchVersions( final ImmutableSet<BranchNodeVersion> set )
    {
        super( set );
    }

    public static NodeBranchVersions from( final BranchNodeVersion... branchNodeVersions )
    {
        return new NodeBranchVersions( ImmutableSet.copyOf( branchNodeVersions ) );
    }

    public static NodeBranchVersions from( final Collection<BranchNodeVersion> branchNodeVersions )
    {
        return new NodeBranchVersions( ImmutableSet.copyOf( branchNodeVersions ) );
    }

    private NodeBranchVersions( final Builder builder )
    {
        super( ImmutableSet.copyOf( builder.branchVersions ) );
    }

    public static NodeBranchVersions empty()
    {
        ImmutableSet<BranchNodeVersion> empty = ImmutableSet.of();
        return new NodeBranchVersions( empty );
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static class Builder
    {
        private final LinkedHashSet<BranchNodeVersion> branchVersions = Sets.newLinkedHashSet();

        public Builder add( final BranchNodeVersion branchNodeVersion )
        {
            this.branchVersions.add( branchNodeVersion );
            return this;
        }

        public NodeBranchVersions build()
        {
            return new NodeBranchVersions( this );
        }

    }

}
