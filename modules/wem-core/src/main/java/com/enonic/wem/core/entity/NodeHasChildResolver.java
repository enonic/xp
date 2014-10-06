package com.enonic.wem.core.entity;

import com.enonic.wem.api.context.Context2;
import com.enonic.wem.core.workspace.WorkspaceContext;
import com.enonic.wem.core.workspace.WorkspaceService;

public class NodeHasChildResolver
{
    private final WorkspaceService workspaceService;


    private NodeHasChildResolver( Builder builder )
    {
        this.workspaceService = builder.workspaceService;
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
        final boolean hasChildren = workspaceService.hasChildren( node.path(), WorkspaceContext.from( Context2.current() ) );

        return Node.newNode( node ).hasChildren( hasChildren ).build();
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private WorkspaceService workspaceService;

        private Builder()
        {
        }

        public Builder workspaceService( final WorkspaceService workspaceService )
        {
            this.workspaceService = workspaceService;
            return this;
        }

        public NodeHasChildResolver build()
        {
            return new NodeHasChildResolver( this );
        }
    }
}
