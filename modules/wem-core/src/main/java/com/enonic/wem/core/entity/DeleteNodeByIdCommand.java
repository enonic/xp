package com.enonic.wem.core.entity;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.Workspace;

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

        final Node nodeToDelete = nodeDao.getById( this.entityId, workspace );

        doDeleteChildIndexDocuments( nodeToDelete, workspace );

        final Node deletedNode = nodeDao.deleteById( entityId, workspace );

        indexService.delete( entityId, workspace );

        return deletedNode;
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
