package com.enonic.wem.core.entity;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Workspace;

final class DeleteNodeByPathCommand
    extends AbstractDeleteNodeCommand
{
    private final NodePath nodePath;

    DeleteNodeByPathCommand( final Builder builder )
    {
        super( builder );
        this.nodePath = builder.nodePath;
    }

    Node execute()
    {
        final Workspace workspace = this.context.getWorkspace();

        final Node nodeToDelete = nodeDao.getByPath( this.nodePath, workspace );

        doDeleteChildIndexDocuments( nodeToDelete, workspace );

        this.nodeDao.deleteById( nodeToDelete.id(), workspace );

        this.indexService.delete( nodeToDelete.id() );

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
