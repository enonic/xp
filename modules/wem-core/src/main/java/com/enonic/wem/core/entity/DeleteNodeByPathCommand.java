package com.enonic.wem.core.entity;

import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;

final class DeleteNodeByPathCommand
    extends AbstractDeleteNodeCommand
{
    private NodePath nodePath;

    DeleteNodeByPathCommand( final Builder builder )
    {
        super( builder );
        this.nodePath = builder.nodePath;
    }

    Node execute()
    {
        final Node nodeToDelete = nodeDao.getByPath( this.nodePath );

        doDeleteChildIndexDocuments( nodeToDelete );

        this.nodeDao.deleteByPath( nodePath );

        this.indexService.deleteEntity( nodeToDelete.id() );

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
