package com.enonic.wem.core.entity;

import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.core.workspace.WorkspaceService;

public class NodeHasChildResolver
{
    private final WorkspaceService workspaceService;

    private final Workspace workspace;

    private NodeHasChildResolver( Builder builder )
    {
        workspaceService = builder.workspaceService;
        workspace = builder.workspace;
    }

    public Nodes resolve( final Nodes nodes )
    {
        final Nodes.Builder populatedNodes = Nodes.create();

        for ( final Node node : nodes )
        {
            populatedNodes.add( doResolve( node ) );
        }

        return populatedNodes.build();
    }

    public Node resolve( final Node node )
    {
        return doResolve( node );
    }

    private Node doResolve( final Node node )
    {
        final boolean hasChildren = workspaceService.hasChildren( node.path(), this.workspace );

        return Node.newNode( node ).hasChildren( hasChildren ).build();
    }


    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private WorkspaceService workspaceService;

        private Workspace workspace;

        private Builder()
        {
        }

        public Builder workspaceService( final WorkspaceService workspaceService )
        {
            this.workspaceService = workspaceService;
            return this;
        }

        public Builder workspace( final Workspace workspace )
        {
            this.workspace = workspace;
            return this;
        }

        public NodeHasChildResolver build()
        {
            return new NodeHasChildResolver( this );
        }
    }
}
