package com.enonic.wem.core.entity;


import java.time.Instant;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.Attachments;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeVersionId;
import com.enonic.wem.api.entity.UpdateNodeParams;
import com.enonic.wem.api.util.Exceptions;
import com.enonic.wem.core.entity.dao.NodeNotFoundException;
import com.enonic.wem.core.version.EntityVersionDocument;
import com.enonic.wem.core.workspace.StoreWorkspaceDocument;

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
            build(), this.context.getRepository() );

        this.workspaceService.store( StoreWorkspaceDocument.create().
            path( updatedNode.path() ).
            parentPath( updatedNode.parent() ).
            id( updatedNode.id() ).
            nodeVersionId( updatedNodeVersionId ).
            build(), this.context );

        this.indexService.index( updatedNode, context.getWorkspace() );

        return NodeHasChildResolver.create().
            workspaceService( this.workspaceService ).
            context( this.context ).
            build().
            resolve( updatedNode );
    }

    private Node getPersistedNode()
    {
        final NodeVersionId currentVersionId = workspaceService.getCurrentVersion( params.getId(), this.context );

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
            entityIndexConfig(
                editResult.getEntityIndexConfig() != null ? editResult.getEntityIndexConfig() : persistedNode.getEntityIndexConfig() );

        return updateNodeBuilder.build();
    }

    static Builder create( final Context context )
    {
        return new Builder( context );
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

        Builder( final Context context )
        {
            super( context );
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
