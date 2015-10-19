package com.enonic.xp.repo.impl.branch.storage;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterators;

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

        final Iterable<String> split = Splitter.on( SEPARATOR ).
            split( value );

        final String nodeIdAsString = Iterators.get( split.iterator(), 0 );
        final String branchName = Iterators.get( split.iterator(), 1 );

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
