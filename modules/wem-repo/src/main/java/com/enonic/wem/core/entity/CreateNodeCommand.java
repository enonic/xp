package com.enonic.wem.core.entity;

import java.time.Instant;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.repo.Attachments;
import com.enonic.wem.repo.CreateNodeParams;
import com.enonic.wem.repo.Node;
import com.enonic.wem.repo.NodeId;
import com.enonic.wem.repo.NodeName;
import com.enonic.wem.repo.NodePath;

public final class CreateNodeCommand
    extends AbstractNodeCommand
{
    private final CreateNodeParams params;

    private CreateNodeCommand( final Builder builder )
    {
        super( builder );

        this.params = builder.params;
    }

    public Node execute()
    {
        Preconditions.checkNotNull( params.getParent(), "Path of parent Node must be specified" );
        Preconditions.checkArgument( params.getParent().isAbsolute(), "Path to parent Node must be absolute: " + params.getParent() );

        final Instant now = Instant.now();

        verifyNotExistsAlready();

        final PrincipalKey creator = PrincipalKey.from( "system:user:admin" );
        final Node.Builder nodeBuilder = Node.newNode().
            id( this.params.getNodeId() != null ? params.getNodeId() : new NodeId() ).
            createdTime( now ).
            modifiedTime( now ).
            creator( creator ).
            modifier( creator ).
            parent( params.getParent() ).
            name( NodeName.from( params.getName() ) ).
            rootDataSet( params.getData() ).
            attachments( params.getAttachments() != null ? params.getAttachments() : Attachments.empty() ).
            indexConfigDocument( params.getIndexConfigDocument() ).
            hasChildren( false ).
            childOrder( params.getChildOrder() != null ? params.getChildOrder() : ChildOrder.defaultOrder() ).
            accessControlList(
                params.getAccessControlList() != null ? params.getAccessControlList() : NodeDefaultAclFactory.create( creator ) );

        final Node newNode = nodeBuilder.build();

        this.doStoreNode( newNode );

        return newNode;
    }

    private void verifyNotExistsAlready()
    {
        NodePath nodePath = NodePath.newNodePath( params.getParent(), params.getName() ).build();

        Node existingNode = doGetByPath( nodePath, false );

        if ( existingNode != null )
        {
            throw new NodeAlreadyExistException( nodePath );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private CreateNodeParams params;

        Builder()
        {
            super();
        }

        public Builder params( final CreateNodeParams params )
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


