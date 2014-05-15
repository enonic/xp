package com.enonic.wem.core.entity;

import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Workspace;

final class DeleteNodeByPathCommand
    extends AbstractDeleteNodeCommand
{
    private final NodePath nodePath;

    private final Workspace workspace = ContentConstants.DEFAULT_WORKSPACE;

    DeleteNodeByPathCommand( final Builder builder )
    {
        super( builder );
        this.nodePath = builder.nodePath;
    }

    Node execute()
    {
        final Node nodeToDelete = nodeDao.getByPath( this.nodePath, this.workspace );

        doDeleteChildIndexDocuments( nodeToDelete, this.workspace );

        this.nodeDao.deleteById( nodeToDelete.id(), this.workspace );

        this.indexService.delete( nodeToDelete.id() );

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
