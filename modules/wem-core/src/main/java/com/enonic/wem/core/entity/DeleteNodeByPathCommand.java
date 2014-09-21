package com.enonic.wem.core.entity;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.NodeVersionId;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.core.entity.dao.NodeNotFoundException;
import com.enonic.wem.core.index.IndexContext;
import com.enonic.wem.core.workspace.WorkspaceContext;

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
        final Workspace workspace = this.context.getWorkspace();

        final NodeVersionId version = this.workspaceService.getByPath( this.nodePath, WorkspaceContext.from( context ) );

        if ( version == null )
        {
            throw new NodeNotFoundException( "Node with path " + this.nodeDao + " not found in workspace " + workspace );
        }

        final Node nodeToDelete = nodeDao.getByVersionId( version );

        doDeleteChildren( nodeToDelete );

        this.workspaceService.delete( nodeToDelete.id(), WorkspaceContext.from( context ) );

        this.indexService.delete( nodeToDelete.id(), IndexContext.from( this.context ) );

        return nodeToDelete;
    }

    static Builder create( final Context context )
    {
        return new Builder( context );
    }

    static class Builder
        extends AbstractDeleteNodeCommand.Builder<Builder>
    {
        private NodePath nodePath;


        Builder( final Context context )
        {
            super( context );
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
