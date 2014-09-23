package com.enonic.wem.core.entity;

import java.time.Instant;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.Attachments;
import com.enonic.wem.api.entity.CreateNodeParams;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeName;
import com.enonic.wem.api.entity.NodeVersionId;
import com.enonic.wem.core.index.IndexContext;
import com.enonic.wem.core.version.EntityVersionDocument;
import com.enonic.wem.core.workspace.StoreWorkspaceDocument;
import com.enonic.wem.core.workspace.WorkspaceContext;

final class CreateNodeCommand
    extends AbstractNodeCommand
{
    private final CreateNodeParams params;

    private CreateNodeCommand( final Builder builder )
    {
        super( builder );

        this.params = builder.params;
    }

    Node execute()
    {
        return doExecute();
    }

    private Node doExecute()
    {
        Preconditions.checkNotNull( params.getParent(), "Path of parent Node must be specified" );
        Preconditions.checkArgument( params.getParent().isAbsolute(), "Path to parent Node must be absolute: " + params.getParent() );

        final Instant now = Instant.now();

        final Node newNode = Node.newNode().
            id( new EntityId() ).
            createdTime( now ).
            modifiedTime( now ).
            creator( UserKey.superUser() ).
            modifier( UserKey.superUser() ).
            parent( params.getParent() ).
            name( NodeName.from( params.getName() ) ).
            rootDataSet( params.getData() ).
            attachments( params.getAttachments() != null ? params.getAttachments() : Attachments.empty() ).
            entityIndexConfig( params.getNodeIndexConfig() ).
            hasChildren( false ).
            build();

        final NodeVersionId persistedNodeVersionId = nodeDao.store( newNode );

        this.workspaceService.store( StoreWorkspaceDocument.create().
            id( newNode.id() ).
            parentPath( newNode.parent() ).
            path( newNode.path() ).
            nodeVersionId( persistedNodeVersionId ).
            build(), WorkspaceContext.from( context ) );

        versionService.store( EntityVersionDocument.create().
            entityId( newNode.id() ).
            nodeVersionId( persistedNodeVersionId ).
            build(), this.context.getRepository() );

        this.indexService.store( newNode, IndexContext.from( this.context ) );

        return newNode;
    }

    static Builder create( final Context context )
    {
        return new Builder( context );
    }

    static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private CreateNodeParams params;

        Builder( final Context context )
        {
            super( context );
        }

        Builder params( final CreateNodeParams params )
        {
            this.params = params;
            return this;
        }

        protected void validate()
        {
            super.validate();
            Preconditions.checkNotNull( params );
        }

        public CreateNodeCommand build()
        {
            return new CreateNodeCommand( this );
        }
    }
}


