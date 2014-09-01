package com.enonic.wem.core.entity;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeVersionId;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.core.entity.dao.NodeNotFoundException;
import com.enonic.wem.core.workspace.query.WorkspaceDeleteQuery;
import com.enonic.wem.core.workspace.query.WorkspaceIdQuery;

final class DeleteNodeByIdCommand
    extends AbstractDeleteNodeCommand
{
    private final EntityId entityId;

    DeleteNodeByIdCommand( final Builder builder )
    {
        super( builder );
        this.entityId = builder.entityId;
    }

    Node execute()
    {
        final Workspace workspace = this.context.getWorkspace();

        final NodeVersionId currentVersion = this.workspaceService.getCurrentVersion( new WorkspaceIdQuery( workspace, this.entityId ) );

        if ( currentVersion == null )
        {
            throw new NodeNotFoundException( "Node with id " + this.entityId + " not found in workspace " + workspace.getName() );
        }

        final Node nodeToDelete = nodeDao.getByVersionId( currentVersion );

        doDeleteChildren( nodeToDelete, workspace );

        workspaceService.delete( new WorkspaceDeleteQuery( workspace, entityId ) );
        indexService.delete( entityId, workspace );

        return nodeToDelete;
    }

    static Builder create( final Context context )
    {
        return new Builder( context );
    }

    static class Builder
        extends AbstractDeleteNodeCommand.Builder<Builder>
    {

        private EntityId entityId;

        Builder( final Context context )
        {
            super( context );
        }

        public Builder entityId( final EntityId entityId )
        {
            this.entityId = entityId;
            return this;
        }

        public DeleteNodeByIdCommand build()
        {
            return new DeleteNodeByIdCommand( this );
        }
    }

}
