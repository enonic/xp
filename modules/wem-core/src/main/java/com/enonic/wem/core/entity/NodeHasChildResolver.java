package com.enonic.wem.core.entity;

import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.core.workspace.WorkspaceService;
import com.enonic.wem.core.workspace.query.WorkspaceHasChildrenQuery;

public class NodeHasChildResolver
{
    private final WorkspaceService workspaceService;

    private final Repository repository;

    private final Workspace workspace;

    private NodeHasChildResolver( Builder builder )
    {
        this.workspaceService = builder.workspaceService;
        this.workspace = builder.workspace;
        this.repository = builder.repository;
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
        final boolean hasChildren = workspaceService.hasChildren( WorkspaceHasChildrenQuery.create().
            parent( node.path() ).
            workspace( this.workspace ).
            repository( this.repository ).
            build() );

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

        private Repository repository;

        private Builder()
        {
        }

        public Builder repository( final Repository repository )
        {
            this.repository = repository;
            return this;
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
