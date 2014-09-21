package com.enonic.wem.core.entity;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.workspace.WorkspaceContext;
import com.enonic.wem.core.workspace.WorkspaceService;

public class NodeHasChildResolver
{
    private final WorkspaceService workspaceService;

    private final Context context;

    private NodeHasChildResolver( Builder builder )
    {
        this.workspaceService = builder.workspaceService;
        this.context = builder.context;
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
        final boolean hasChildren = workspaceService.hasChildren( node.path(), WorkspaceContext.from( this.context ) );

        return Node.newNode( node ).hasChildren( hasChildren ).build();
    }


    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private WorkspaceService workspaceService;

        private Context context;

        private Builder()
        {
        }

        public Builder context( final Context context )
        {
            this.context = context;
            return this;
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
