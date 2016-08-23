package com.enonic.xp.repo.impl.branch.storage;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import com.enonic.xp.branch.BranchId;
import com.enonic.xp.node.NodeId;

public class BranchDocumentId
{
    private static final String SEPARATOR = "_";

    private final String value;

    private final NodeId nodeId;

    private final BranchId branchId;

    public BranchDocumentId( final NodeId nodeId, final BranchId branchId )
    {
        Preconditions.checkNotNull( nodeId );
        Preconditions.checkNotNull( branchId );

        this.value = nodeId + SEPARATOR + branchId.getValue();
        this.nodeId = nodeId;
        this.branchId = branchId;
    }

    private BranchDocumentId( final String value, final String nodeIdsAsString, final String branchName )
    {
        this.value = value;
        this.nodeId = NodeId.from( nodeIdsAsString );
        this.branchId = BranchId.from( branchName );
    }

    public static BranchDocumentId from( final NodeId nodeId, final BranchId branchId )
    {
        return new BranchDocumentId( nodeId, branchId );
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

    public BranchId getBranchId()
    {
        return branchId;
    }

    @Override
    public String toString()
    {
        return value;
    }
}
