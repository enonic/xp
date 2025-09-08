package com.enonic.xp.repo.impl.node;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.BinaryAttachment;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.InsertManualStrategy;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBinaryReferenceException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIdExistsException;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.binary.BinaryService;
import com.enonic.xp.repo.impl.storage.StoreNodeParams;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static com.enonic.xp.repo.impl.node.NodeConstants.CLOCK;

public final class CreateNodeCommand
    extends AbstractNodeCommand
{
    private final CreateNodeParams params;

    private final Instant timestamp;

    private final BinaryService binaryService;

    private final boolean skipVerification;

    private CreateNodeCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.timestamp = builder.timestamp;
        this.binaryService = builder.binaryService;
        this.skipVerification = builder.skipVerification;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public Node execute()
    {
        if ( !skipVerification )
        {
            NodeHelper.runAsAdmin( this::verifyNotExistsAlready );
        }
        final Node parentNode = NodeHelper.runAsAdmin( this::getParentNode );

        NodePermissionsResolver.requireContextUserPermissionOrAdmin( Permission.CREATE, parentNode );

        final PrincipalKey user = getCurrentPrincipalKey();

        final AccessControlList permissions = params.isInheritPermissions() ? parentNode.getPermissions() : getAccessControlEntries( user );

        final Long manualOrderValue = resolvePotentialManualOrderValue( parentNode );

        final AttachedBinaries attachedBinaries = storeAndAttachBinaries();

        final Node.Builder nodeBuilder = Node.create()
            .id( this.params.getNodeId() != null ? params.getNodeId() : new NodeId() )
            .parentPath( params.getParent() )
            .name( params.getName() )
            .data( params.getData() )
            .indexConfigDocument( params.getIndexConfigDocument() )
            .childOrder( params.getChildOrder() != null ? params.getChildOrder() : ChildOrder.defaultOrder() )
            .manualOrderValue( manualOrderValue )
            .permissions( permissions )
            .nodeType( params.getNodeType() != null ? params.getNodeType() : NodeType.DEFAULT_NODE_COLLECTION )
            .attachedBinaries( attachedBinaries )
            .timestamp( this.timestamp != null ? this.timestamp : Instant.now( CLOCK ) );

        final Node newNode = this.nodeStorageService.store( StoreNodeParams.newVersion( nodeBuilder.build() ),
                                                            InternalContext.from( ContextAccessor.current() ) ).node();

        refresh( params.getRefresh() );
        return newNode;
    }

    private AttachedBinaries storeAndAttachBinaries()
    {
        final PropertyTree data = params.getData();

        final AttachedBinaries.Builder builder = AttachedBinaries.create();

        final List<Property> binaryReferences = data.getProperties( ValueTypes.BINARY_REFERENCE );

        final RepositoryId repositoryId = ContextAccessor.current().getRepositoryId();
        for ( final Property binaryRef : binaryReferences )
        {
            final BinaryAttachment binaryAttachment = this.params.getBinaryAttachments().get( binaryRef.getBinaryReference() );

            if ( binaryAttachment == null )
            {
                throw new NodeBinaryReferenceException( "No binary with reference " + binaryRef + " attached in createNodeParams" );
            }

            final AttachedBinary attachedBinary = this.binaryService.store( repositoryId, binaryAttachment );
            builder.add( attachedBinary );
        }

        return builder.build();
    }

    private AccessControlList getAccessControlEntries( final PrincipalKey creator )
    {
        final AccessControlList paramPermissions = params.getPermissions();

        if ( paramPermissions == null || paramPermissions.isEmpty() )
        {
            return NodeDefaultAclFactory.create( creator );
        }
        else
        {
            return paramPermissions;
        }
    }

    private Node getParentNode()
    {
        final Node parentNode = doGetByPath( params.getParent() );

        if ( parentNode == null )
        {
            throw new NodeNotFoundException(
                "Cannot create node with name " + params.getName() + ", parent '" + params.getParent() + "' not found" );
        }
        return parentNode;
    }

    private Long resolvePotentialManualOrderValue( final Node parentNode )
    {
        if ( parentNode.isRoot() )
        {
            return null;
        }

        if ( parentNode.getChildOrder() != null && parentNode.getChildOrder().isManualOrder() )
        {
            final InsertManualStrategy insertManualStrategy = this.params.getInsertManualStrategy();

            if ( InsertManualStrategy.MANUAL.equals( insertManualStrategy ) )
            {
                return params.getManualOrderValue();
            }
            else
            {
                return ResolveInsertOrderValueCommand.create( this )
                    .parentPath( parentNode.path() )
                    .build()
                    .insert( InsertManualStrategy.LAST.equals( insertManualStrategy ) );
            }
        }

        return null;
    }


    private void verifyNotExistsAlready()
    {
        if ( this.params.getNodeId() != null )
        {
            final Node existingNode = doGetById( this.params.getNodeId() );

            if ( existingNode != null )
            {
                throw new NodeIdExistsException( existingNode.id() );
            }
        }

        NodePath nodePath = new NodePath( params.getParent(), params.getName() );

        CheckNodeExistsCommand.create( this ).nodePath( nodePath ).throwIfExists().build().execute();
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private CreateNodeParams params;

        private Instant timestamp;

        private BinaryService binaryService;

        private boolean skipVerification;

        private Builder()
        {
            super();
        }

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder params( final CreateNodeParams params )
        {
            this.params = params;
            return this;
        }

        public Builder binaryService( final BinaryService binaryService )
        {
            this.binaryService = binaryService;
            return this;
        }

        public Builder timestamp( final Instant timestamp )
        {
            this.timestamp = timestamp;
            return this;
        }

        public Builder skipVerification( boolean skipVerification )
        {
            this.skipVerification = skipVerification;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Objects.requireNonNull( params, "params cannot be null" );
        }

        public CreateNodeCommand build()
        {
            this.validate();
            return new CreateNodeCommand( this );
        }
    }
}


