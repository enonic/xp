package com.enonic.xp.repo.impl.branch.storage;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeId;

public final class BranchDocumentId
{
    private static final String SEPARATOR = "_";

    private final NodeId nodeId;

    private final Branch branch;

    private BranchDocumentId( final NodeId nodeId, final Branch branch )
    {
        this.nodeId = Objects.requireNonNull( nodeId );
        this.branch = Objects.requireNonNull( branch );
    }

    public static BranchDocumentId from( final NodeId nodeId, final Branch branch )
    {
        return new BranchDocumentId( nodeId, branch );
    }

    public static BranchDocumentId from( final String value )
    {
        final int separator = value.lastIndexOf( SEPARATOR );
        Preconditions.checkArgument( separator != -1 && separator != 0 && separator != value.length() - 1,
                                     "Invalid format of branch-key: %s", value );

        final String nodeIdAsString = value.substring( 0, separator );
        final String branchName = value.substring( separator + 1 );

        return new BranchDocumentId( NodeId.from( nodeIdAsString ), Branch.from( branchName ) );
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public Branch getBranch()
    {
        return branch;
    }

    @Override
    public String toString()
    {
        return nodeId + SEPARATOR + branch;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final BranchDocumentId that = (BranchDocumentId) o;

        return nodeId.equals( that.nodeId ) && branch.equals( that.branch );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( nodeId, branch );
    }
}
