package com.enonic.wem.repo.internal.storage.branch;

import java.util.Collection;
import java.util.LinkedList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.enonic.xp.support.AbstractImmutableEntityList;

public class BranchNodeVersions
    extends AbstractImmutableEntityList<BranchNodeVersion>
{
    private BranchNodeVersions( final ImmutableList<BranchNodeVersion> list )
    {
        super( list );
    }

    public static BranchNodeVersions from( final Collection<BranchNodeVersion> branchNodeVersions )
    {
        return new BranchNodeVersions( ImmutableList.copyOf( branchNodeVersions ) );
    }

    private BranchNodeVersions( final Builder builder )
    {
        super( ImmutableList.copyOf( builder.branchVersions ) );
    }


    public static Builder create()
    {
        return new Builder();
    }


    public static class Builder
    {
        private final LinkedList<BranchNodeVersion> branchVersions = Lists.newLinkedList();

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
