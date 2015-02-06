package com.enonic.wem.repo.internal.entity;

import java.io.IOException;
import java.util.Set;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.data.ValueTypes;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.node.AttachedBinaries;
import com.enonic.wem.api.node.AttachedBinary;
import com.enonic.wem.api.node.BinaryAttachment;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.FindNodesByParentParams;
import com.enonic.wem.api.node.FindNodesByParentResult;
import com.enonic.wem.api.node.InsertManualStrategy;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeAlreadyExistAtPathException;
import com.enonic.wem.api.node.NodeBinaryReferenceException;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeIdExistsException;
import com.enonic.wem.api.node.NodeName;
import com.enonic.wem.api.node.NodeNotFoundException;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeType;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.acl.AccessControlList;
import com.enonic.wem.repo.internal.blob.Blob;
import com.enonic.wem.repo.internal.blob.BlobStore;

public final class CreateNodeCommand
    extends AbstractNodeCommand
{
    private final CreateNodeParams params;

    private final BlobStore binaryBlobStore;

    private CreateNodeCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.binaryBlobStore = builder.binaryBlobStore;
    }

    public Node execute()
    {
        Preconditions.checkNotNull( params.getParent(), "Path of parent Node must be specified" );
        Preconditions.checkArgument( params.getParent().isAbsolute(), "Path to parent Node must be absolute: " + params.getParent() );

        runAsAdmin( this::verifyNotExistsAlready );
        runAsAdmin( this::verifyParentExists );

        final PrincipalKey user = getCurrentPrincipalKey();

        final AccessControlList permissions = getAccessControlEntries( user );

        final Long manualOrderValue = runAsAdmin( this::resolvePotentialManualOrderValue );

        final AttachedBinaries attachedBinaries = storeAndAttachBinaries();

        final Node.Builder nodeBuilder = Node.newNode().
            id( this.params.getNodeId() != null ? params.getNodeId() : new NodeId() ).
            parentPath( params.getParent() ).
            name( NodeName.from( params.getName() ) ).
            data( params.getData() ).
            indexConfigDocument( params.getIndexConfigDocument() ).
            hasChildren( false ).
            childOrder( params.getChildOrder() != null ? params.getChildOrder() : ChildOrder.defaultOrder() ).
            manualOrderValue( manualOrderValue ).
            permissions( permissions ).
            inheritPermissions( params.inheritPermissions() ).
            nodeType( params.getNodeType() != null ? params.getNodeType() : NodeType.DEFAULT_NODE_COLLECTION ).
            attachedBinaries( attachedBinaries );

        final Node newNode = nodeBuilder.build();

        this.doStoreNode( newNode );

        return newNode;
    }

    private AttachedBinaries storeAndAttachBinaries()
    {
        final PropertyTree data = params.getData();

        final Set<Property> binaryReferences = data.getByValueType( ValueTypes.BINARY_REFERENCE );

        final AttachedBinaries.Builder builder = AttachedBinaries.create();

        for ( final Property binaryRef : binaryReferences )
        {
            final BinaryAttachment binaryAttachment = this.params.getBinaryAttachments().get( binaryRef.getBinaryReference() );

            if ( binaryAttachment == null )
            {
                throw new NodeBinaryReferenceException( "No binary with reference " + binaryRef + " attached in createNodeParams" );
            }

            try
            {
                final Blob blob = this.binaryBlobStore.addRecord( binaryAttachment.getByteSource().openStream() );
                builder.add( new AttachedBinary( binaryAttachment.getReference(), blob.getKey() ) );
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }

        return builder.build();
    }

    private AccessControlList getAccessControlEntries( final PrincipalKey creator )
    {
        AccessControlList paramPermissions = params.getPermissions();

        if ( paramPermissions == null || paramPermissions.isEmpty() )
        {
            paramPermissions = NodeDefaultAclFactory.create( creator );
        }

        return evaluatePermissions( params.getParent(), params.inheritPermissions(), paramPermissions );
    }

    private void verifyParentExists()
    {
        if ( NodePath.ROOT.equals( params.getParent() ) )
        {
            return;
        }

        final Node parentNode = doGetByPath( params.getParent(), false );

        if ( parentNode == null )
        {
            throw new NodeNotFoundException(
                "Cannot create node with name " + params.getName() + ", parent '" + params.getParent() + "' not found" );
        }
    }

    private Long resolvePotentialManualOrderValue()
    {
        if ( NodePath.ROOT.equals( params.getParent() ) )
        {
            return null;
        }

        final Node parentNode = doGetByPath( params.getParent(), false );

        if ( parentNode == null )
        {
            return null;
        }

        if ( parentNode.getChildOrder() != null && parentNode.getChildOrder().isManualOrder() )
        {
            return doResolveManualOrderValue( parentNode );
        }

        return null;
    }

    private Long doResolveManualOrderValue( final Node parentNode )
    {
        final InsertManualStrategy insertManualStrategy = this.params.getInsertManualStrategy();

        if ( InsertManualStrategy.MANUAL.equals( insertManualStrategy ) )
        {
            return params.getManualOrderValue();
        }
        else
        {
            return resolveFromQuery( parentNode, insertManualStrategy );
        }
    }

    private Long resolveFromQuery( final Node parentNode, final InsertManualStrategy insertManualStrategy )
    {
        final ChildOrder childOrder =
            insertManualStrategy.equals( InsertManualStrategy.LAST ) ? ChildOrder.reverseManualOrder() : ChildOrder.manualOrder();

        final FindNodesByParentResult findNodesByParentResult = doFindNodesByParent( FindNodesByParentParams.create().
            parentPath( parentNode.path() ).
            childOrder( childOrder ).
            size( 1 ).
            build() );

        if ( findNodesByParentResult.isEmpty() )
        {
            return NodeManualOrderValueResolver.START_ORDER_VALUE;
        }
        else
        {
            if ( InsertManualStrategy.LAST.equals( insertManualStrategy ) )
            {
                return insertAsLast( findNodesByParentResult );
            }
            else
            {
                return insertAsFirst( findNodesByParentResult );
            }
        }
    }

    private Long insertAsFirst( final FindNodesByParentResult findNodesByParentResult )
    {
        final Node first = findNodesByParentResult.getNodes().first();

        if ( first.getManualOrderValue() == null )
        {
            throw new IllegalArgumentException( "Expected that node " + first +
                                                    " should have manualOrderValue since parent childOrder = manualOrderValue, but value was null" );
        }

        return first.getManualOrderValue() + NodeManualOrderValueResolver.ORDER_SPACE;
    }

    private Long insertAsLast( final FindNodesByParentResult findNodesByParentResult )
    {
        final Node first = findNodesByParentResult.getNodes().first();

        if ( first.getManualOrderValue() == null )
        {
            throw new IllegalArgumentException( "Expected that node " + first +
                                                    " should have manualOrderValue since parent childOrder = manualOrderValue, but value was null" );
        }

        return first.getManualOrderValue() - NodeManualOrderValueResolver.ORDER_SPACE;
    }

    private void verifyNotExistsAlready()
    {
        if ( this.params.getNodeId() != null )
        {
            final Node existingNode = doGetById( this.params.getNodeId(), false );

            if ( existingNode != null )
            {
                throw new NodeIdExistsException( existingNode.id() );
            }
        }

        NodePath nodePath = NodePath.newNodePath( params.getParent(), params.getName() ).build();

        Node existingNode = doGetByPath( nodePath, false );

        if ( existingNode != null )
        {
            throw new NodeAlreadyExistAtPathException( nodePath );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private CreateNodeParams params;

        private BlobStore binaryBlobStore;

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

        public Builder binaryBlobStore( final BlobStore blobStore )
        {
            this.binaryBlobStore = blobStore;
            return this;
        }

        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( params );
        }

        public CreateNodeCommand build()
        {
            this.validate();
            return new CreateNodeCommand( this );
        }
    }
}


