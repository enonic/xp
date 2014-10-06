package com.enonic.wem.core.entity;


import java.time.Instant;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.util.Exceptions;
import com.enonic.wem.core.entity.dao.NodeNotFoundException;
import com.enonic.wem.core.index.IndexContext;
import com.enonic.wem.core.version.EntityVersionDocument;
import com.enonic.wem.core.workspace.StoreWorkspaceDocument;
import com.enonic.wem.core.workspace.WorkspaceContext;

final class UpdateNodeCommand
    extends AbstractNodeCommand
{
    private final UpdateNodeParams params;

    private UpdateNodeCommand( final Builder builder )
    {
        super( builder );

        this.params = builder.params;
    }

    Node execute()
    {
        this.params.validate();

        try
        {
            return doExecute();
        }
        catch ( final Exception e )
        {
            throw Exceptions.newRutime( "Error updating node " + params.getId() ).withCause( e );
        }
    }

    private Node doExecute()
    {
        final Context context = Context.current();

        final Node persistedNode = getPersistedNode();

        final Node.EditBuilder editBuilder = params.getEditor().edit( persistedNode );
        if ( !editBuilder.isChanges() )
        {
            return persistedNode;
        }

        final Node updatedNode = createUpdatedNode( persistedNode, editBuilder );

        final NodeVersionId updatedNodeVersionId = nodeDao.store( updatedNode );

        this.versionService.store( EntityVersionDocument.create().
            entityId( updatedNode.id() ).
            nodeVersionId( updatedNodeVersionId ).
            build(), context.getRepositoryId() );

        this.workspaceService.store( StoreWorkspaceDocument.create().
            path( updatedNode.path() ).
            parentPath( updatedNode.parent() ).
            id( updatedNode.id() ).
            nodeVersionId( updatedNodeVersionId ).
            build(), WorkspaceContext.from( context ) );

        this.indexService.store( updatedNode, IndexContext.from( context ) );

        return NodeHasChildResolver.create().
            workspaceService( this.workspaceService ).
            build().
            resolve( updatedNode );
    }

    private Node getPersistedNode()
    {
        final NodeVersionId currentVersionId =
            workspaceService.getCurrentVersion( params.getId(), WorkspaceContext.from( Context.current() ) );

        if ( currentVersionId == null )
        {
            throw new NodeNotFoundException( "Node to be updated not found" );
        }

        return nodeDao.getByVersionId( currentVersionId );
    }

    private Node createUpdatedNode( final Node persistedNode, final Node.EditBuilder editBuilder )
    {
        final Node editResult = editBuilder.build();
        persistedNode.checkIllegalEdit( editResult );

        final Instant now = Instant.now();

        final Node.Builder updateNodeBuilder = Node.newNode( persistedNode ).
            modifiedTime( now ).
            modifier( UserKey.superUser() ).
            rootDataSet( editResult.data() ).
            attachments( syncronizeAttachments( editResult.attachments(), persistedNode ) ).
            indexConfigDocument( editResult.getIndexConfigDocument() != null
                                     ? editResult.getIndexConfigDocument()
                                     : persistedNode.getIndexConfigDocument() );

        return updateNodeBuilder.build();
    }

    static Builder create()
    {
        return new Builder();
    }

    private Attachments syncronizeAttachments( final Attachments attachments, final Node persistedNode )
    {
        final Attachments persistedAttachments = persistedNode.attachments();

        if ( attachments == null )
        {
            return persistedAttachments;
        }

        return attachments;

    }

    static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private UpdateNodeParams params;

        Builder()
        {
            super();
        }

        Builder params( final UpdateNodeParams params )
        {
            this.params = params;
            return this;
        }

        UpdateNodeCommand build()
        {
            return new UpdateNodeCommand( this );
        }
    }

}
