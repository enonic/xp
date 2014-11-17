package com.enonic.wem.core.workspace;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterators;

import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.api.node.NodeId;

public class WorkspaceDocumentId
{
    private static final String SEPARATOR = "_";

    private final String value;

    private final NodeId nodeId;

    private final Workspace workspace;

    public WorkspaceDocumentId( final NodeId nodeId, final Workspace workspace )
    {
        Preconditions.checkNotNull( nodeId );
        Preconditions.checkNotNull( workspace );

        this.value = nodeId + SEPARATOR + workspace.getName();
        this.nodeId = nodeId;
        this.workspace = workspace;
    }

    private WorkspaceDocumentId( final String value, final String nodeIdsAsString, final String workspaceName )
    {
        this.value = value;
        this.nodeId = NodeId.from( nodeIdsAsString );
        this.workspace = Workspace.from( workspaceName );
    }

    public static WorkspaceDocumentId from( final String value )
    {
        if ( !value.contains( SEPARATOR ) )
        {
            throw new IllegalArgumentException( "Invalid format of workspace-key: " + value );
        }

        final Iterable<String> split = Splitter.on( SEPARATOR ).
            split( value );

        final String nodeIdAsString = Iterators.get( split.iterator(), 0 );
        final String workspaceName = Iterators.get( split.iterator(), 1 );

        Preconditions.checkArgument( !Strings.isNullOrEmpty( nodeIdAsString ) );
        Preconditions.checkArgument( !Strings.isNullOrEmpty( workspaceName ) );

        return new WorkspaceDocumentId( value, nodeIdAsString, workspaceName );
    }

    public String getValue()
    {
        return value;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public Workspace getWorkspace()
    {
        return workspace;
    }

    @Override
    public String toString()
    {
        return value;
    }
}
