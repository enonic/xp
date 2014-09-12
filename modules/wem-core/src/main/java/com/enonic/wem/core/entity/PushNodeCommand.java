package com.enonic.wem.core.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeVersionId;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.core.workspace.StoreWorkspaceDocument;
import com.enonic.wem.core.workspace.query.WorkspaceIdQuery;

public class PushNodeCommand
    extends AbstractNodeCommand
{
    private final Workspace target;

    private final EntityId id;

    private PushNodeCommand( final Builder builder )
    {
        super( builder );
        this.target = builder.target;
        this.id = builder.id;
    }

    public static Builder create( final Context context )
    {
        return new Builder( context );
    }


    Node execute()
    {
        final Workspace workspace = this.context.getWorkspace();

        final NodeVersionId currentVersion = this.workspaceService.getCurrentVersion( WorkspaceIdQuery.create().
            workspace( workspace ).
            repository( this.context.getRepository() ).
            entityId( this.id ).
            build() );

        final Node currentNode = nodeDao.getByVersionId( currentVersion );

        this.workspaceService.store( StoreWorkspaceDocument.create().
            nodeVersionId( currentVersion ).
            workspace( this.target ).
            id( this.id ).
            path( currentNode.path() ).
            parentPath( currentNode.parent() ).
            repository( this.context.getRepository() ).
            build() );

        this.indexService.index( currentNode, this.target );

        return NodeHasChildResolver.create().
            workspace( context.getWorkspace() ).
            repository( context.getRepository() ).
            workspaceService( this.workspaceService ).
            build().
            resolve( currentNode );
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private Workspace target;

        private EntityId id;

        Builder( final Context context )
        {
            super( context );
        }

        public Builder target( final Workspace target )
        {
            this.target = target;
            return this;
        }

        public Builder id( final EntityId entityId )
        {
            this.id = entityId;
            return this;
        }

        public PushNodeCommand build()
        {
            validate();
            return new PushNodeCommand( this );
        }

        protected void validate()
        {
            Preconditions.checkNotNull( target );
            Preconditions.checkNotNull( id );
        }
    }


}
