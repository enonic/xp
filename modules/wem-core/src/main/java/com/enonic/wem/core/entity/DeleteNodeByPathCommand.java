package com.enonic.wem.core.entity;

import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Workspace;

final class DeleteNodeByPathCommand
    extends AbstractDeleteNodeCommand
{
    private final NodePath nodePath;

    private final Workspace workspace;

    DeleteNodeByPathCommand( final Builder builder )
    {
        super( builder );
        this.nodePath = builder.nodePath;
        this.workspace = builder.workspace;
    }

    Node execute()
    {
        final Node nodeToDelete = nodeDao.getByPath( this.nodePath, this.workspace );

        doDeleteChildIndexDocuments( nodeToDelete, this.workspace );

        this.nodeDao.deleteByPath( nodePath, this.workspace );

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

        private Workspace workspace;

        Builder nodePath( final NodePath nodePath )
        {
            this.nodePath = nodePath;
            return this;
        }

        Builder workspace( final Workspace workspace )
        {
            this.workspace = workspace;
            return this;
        }

        DeleteNodeByPathCommand build()
        {
            return new DeleteNodeByPathCommand( this );
        }
    }

}
