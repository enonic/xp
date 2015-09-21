package com.enonic.wem.repo.internal.storage.branch;

import java.util.Collection;
import java.util.LinkedHashSet;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.xp.support.AbstractImmutableEntitySet;

public class BranchNodeVersions
    extends AbstractImmutableEntitySet<BranchNodeVersion>
{
    private BranchNodeVersions( final ImmutableSet<BranchNodeVersion> set )
    {
        super( set );
    }

    public static BranchNodeVersions from( final BranchNodeVersion... branchNodeVersions )
    {
        return new BranchNodeVersions( ImmutableSet.copyOf( branchNodeVersions ) );
    }

    public static BranchNodeVersions from( final Collection<BranchNodeVersion> branchNodeVersions )
    {
        return new BranchNodeVersions( ImmutableSet.copyOf( branchNodeVersions ) );
    }

    private BranchNodeVersions( final Builder builder )
    {
        super( ImmutableSet.copyOf( builder.branchVersions ) );
    }

    public static BranchNodeVersions empty()
    {
        ImmutableSet<BranchNodeVersion> empty = ImmutableSet.of();
        return new BranchNodeVersions( empty );
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

        public BranchNodeVersions build()
        {
            return new BranchNodeVersions( this );
        }

    }

}
