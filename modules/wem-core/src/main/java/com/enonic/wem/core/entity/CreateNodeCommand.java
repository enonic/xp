package com.enonic.wem.core.entity;

import java.time.Instant;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.core.index.IndexContext;
import com.enonic.wem.core.version.NodeVersionDocument;
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
            id( new NodeId() ).
            createdTime( now ).
            modifiedTime( now ).
            creator( UserKey.superUser() ).
            modifier( UserKey.superUser() ).
            parent( params.getParent() ).
            name( NodeName.from( params.getName() ) ).
            rootDataSet( params.getData() ).
            attachments( params.getAttachments() != null ? params.getAttachments() : Attachments.empty() ).
            indexConfigDocument( params.getIndexConfigDocument() ).
            hasChildren( false ).
            build();

        final NodeVersionId persistedNodeVersionId = nodeDao.store( newNode );

        this.workspaceService.store( StoreWorkspaceDocument.create().
            id( newNode.id() ).
            parentPath( newNode.parent() ).
            path( newNode.path() ).
            nodeVersionId( persistedNodeVersionId ).
            build(), WorkspaceContext.from( Context.current() ) );

        versionService.store( NodeVersionDocument.create().
            nodeId( newNode.id() ).
            nodeVersionId( persistedNodeVersionId ).
            build(), Context.current().getRepositoryId() );

        this.indexService.store( newNode, IndexContext.from( Context.current() ) );

        return newNode;
    }

    static Builder create()
    {
        return new Builder();
    }

    static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private CreateNodeParams params;

        Builder()
        {
            super();
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


