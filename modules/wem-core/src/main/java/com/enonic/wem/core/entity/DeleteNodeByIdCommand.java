package com.enonic.wem.core.entity;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;

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
        final Node nodeToDelete = nodeDao.getById( this.entityId, this.workspace );

        doDeleteChildIndexDocuments( nodeToDelete, this.workspace );

        final Node deletedNode = nodeDao.deleteById( entityId, this.workspace );

        indexService.delete( entityId );

        return deletedNode;
    }

    static Builder create()
    {
        return new Builder();
    }

    static class Builder
        extends AbstractDeleteNodeCommand.Builder<Builder>
    {

        private EntityId entityId;

        public DeleteNodeByIdCommand build()
        {
            return new DeleteNodeByIdCommand( this );
        }

        public Builder entityId( final EntityId entityId )
        {
            this.entityId = entityId;
            return this;
        }
    }


}
