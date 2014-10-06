package com.enonic.wem.core.entity;

import com.enonic.wem.api.context.Context2;
import com.enonic.wem.core.index.IndexContext;
import com.enonic.wem.core.workspace.WorkspaceContext;

final class DeleteNodeByIdCommand
    extends AbstractDeleteNodeCommand
{
    private final EntityId entityId;

    private DeleteNodeByIdCommand( final Builder builder )
    {
        super( builder );
        this.entityId = builder.entityId;
    }

    Node execute()
    {
        final Context2 context = Context2.current();

        final NodeVersionId currentVersion = this.workspaceService.getCurrentVersion( this.entityId, WorkspaceContext.from( context ) );

        final Node nodeToDelete = this.nodeDao.getByVersionId( currentVersion );

        doDeleteChildren( nodeToDelete );

        workspaceService.delete( entityId, WorkspaceContext.from( context ) );

        indexService.delete( entityId, IndexContext.from( context ) );

        return nodeToDelete;
    }

    static Builder create()
    {
        return new Builder();
    }

    static class Builder
        extends AbstractDeleteNodeCommand.Builder<Builder>
    {

        private EntityId entityId;

        Builder()
        {
            super();
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
