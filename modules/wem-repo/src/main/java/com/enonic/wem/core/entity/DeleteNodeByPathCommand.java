package com.enonic.wem.core.entity;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.repo.NodeNotFoundException;
import com.enonic.wem.core.index.IndexContext;
import com.enonic.wem.core.workspace.WorkspaceContext;
import com.enonic.wem.repo.Node;
import com.enonic.wem.repo.NodePath;
import com.enonic.wem.repo.NodeVersionId;

final class DeleteNodeByPathCommand
    extends AbstractDeleteNodeCommand
{
    private final NodePath nodePath;

    private DeleteNodeByPathCommand( final Builder builder )
    {
        super( builder );
        this.nodePath = builder.nodePath;
    }

    Node execute()
    {
        final Context context = ContextAccessor.current();

        final Workspace workspace = context.getWorkspace();

        final NodeVersionId version = this.queryService.get( this.nodePath, IndexContext.from( context ) );

        if ( version == null )
        {
            throw new NodeNotFoundException( "Node with path " + this.nodeDao + " not found in workspace " + workspace );
        }

        final Node nodeToDelete = nodeDao.getByVersionId( version );

        doDeleteChildren( nodeToDelete );

        this.workspaceService.delete( nodeToDelete.id(), WorkspaceContext.from( context ) );

        this.indexService.delete( nodeToDelete.id(), IndexContext.from( context ) );

        return nodeToDelete;
    }

    static Builder create()
    {
        return new Builder();
    }

    static class Builder
        extends AbstractDeleteNodeCommand.Builder<Builder>
    {
        private NodePath nodePath;


        Builder()
        {
            super();
        }

        Builder nodePath( final NodePath nodePath )
        {
            this.nodePath = nodePath;
            return this;
        }

        DeleteNodeByPathCommand build()
        {
            return new DeleteNodeByPathCommand( this );
        }
    }

}
