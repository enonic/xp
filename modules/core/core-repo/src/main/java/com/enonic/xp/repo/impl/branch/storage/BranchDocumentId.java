package com.enonic.xp.repo.impl.branch.storage;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeId;

public class BranchDocumentId
{
    private static final String SEPARATOR = "_";

    private final String value;

    private final NodeId nodeId;

    private final Branch branch;

    public BranchDocumentId( final NodeId nodeId, final Branch branch )
    {
        Preconditions.checkNotNull( nodeId );
        Preconditions.checkNotNull( branch );

        this.value = nodeId + SEPARATOR + branch.getName();
        this.nodeId = nodeId;
        this.branch = branch;
    }

    private BranchDocumentId( final String value, final String nodeIdsAsString, final String branchName )
    {
        this.value = value;
        this.nodeId = NodeId.from( nodeIdsAsString );
        this.branch = Branch.from( branchName );
    }

    public static BranchDocumentId from( final String value )
    {
        if ( !value.contains( SEPARATOR ) )
        {
            throw new IllegalArgumentException( "Invalid format of branch-key: " + value );
        }

        final int separator = value.lastIndexOf( SEPARATOR );
        final String nodeIdAsString = value.substring( 0, separator );
        final String branchName = value.substring( separator + 1, value.length() );

        Preconditions.checkArgument( !Strings.isNullOrEmpty( nodeIdAsString ) );
        Preconditions.checkArgument( !Strings.isNullOrEmpty( branchName ) );

        return new BranchDocumentId( value, nodeIdAsString, branchName );
    }

    public String getValue()
    {
        return value;
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
        return value;
    }
}
