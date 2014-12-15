package com.enonic.wem.repo.internal.entity;


import java.time.Instant;

import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.node.Attachments;
import com.enonic.wem.api.node.EditableNode;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.UpdateNodeParams;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.acl.AccessControlList;
import com.enonic.wem.api.security.auth.AuthenticationInfo;
import com.enonic.wem.api.util.Exceptions;

public final class UpdateNodeCommand
    extends AbstractNodeCommand
{
    private final UpdateNodeParams params;

    private UpdateNodeCommand( final Builder builder )
    {
        super( builder );

        this.params = builder.params;
    }

    public Node execute()
    {
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
        final Node persistedNode = doGetById( params.getId(), false );

        final EditableNode editableNode = new EditableNode( persistedNode );
        params.getEditor().edit( editableNode );
        final Node editedNode = editableNode.build();
        if ( editedNode.equals( persistedNode ) )
        {
            return persistedNode;
        }

        final Node updatedNode = createUpdatedNode( persistedNode, editedNode );

        doStoreNode( updatedNode );

        return NodeHasChildResolver.create().
            queryService( this.queryService ).
            build().
            resolve( updatedNode );
    }

    private Node createUpdatedNode( final Node persistedNode, final Node editedNode )
    {
        final Instant now = Instant.now();
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        final PrincipalKey modifier =
            authInfo != null && authInfo.isAuthenticated() ? authInfo.getUser().getKey() : PrincipalKey.from( "user:system:admin" );

        final NodePath parentPath = editedNode.path().getParentPath();
        //final AccessControlList paramPerm = editedNode.getPermissions() == null ? AccessControlList.empty() : editedNode.getPermissions();
        final AccessControlList permissions =
            evaluatePermissions( parentPath, editedNode.inheritsPermissions(), editedNode.getPermissions() );
        final Attachments attachments = synchronizeAttachments( editedNode.attachments(), persistedNode );

        final Node.Builder updateNodeBuilder = Node.newNode( editedNode ).
            modifiedTime( now ).
            modifier( modifier ).
            attachments( attachments ).
            permissions( permissions );
        return updateNodeBuilder.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    private Attachments synchronizeAttachments( final Attachments attachments, final Node persistedNode )
    {
        final Attachments persistedAttachments = persistedNode.attachments();

        if ( attachments == null )
        {
            return persistedAttachments;
        }

        return attachments;
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private UpdateNodeParams params;

        private Builder()
        {
            super();
        }

        public Builder params( final UpdateNodeParams params )
        {
            this.params = params;
            return this;
        }

        public UpdateNodeCommand build()
        {
            return new UpdateNodeCommand( this );
        }
    }

}
