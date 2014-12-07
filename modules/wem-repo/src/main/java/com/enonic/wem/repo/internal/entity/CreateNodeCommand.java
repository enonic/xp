package com.enonic.wem.repo.internal.entity;

import java.time.Instant;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.node.Attachments;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.FindNodesByParentParams;
import com.enonic.wem.api.node.FindNodesByParentResult;
import com.enonic.wem.api.node.InsertManualStrategy;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeAlreadyExistException;
import com.enonic.wem.api.node.NodeCollection;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeName;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.acl.AccessControlList;
import com.enonic.wem.api.security.auth.AuthenticationInfo;

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
        verifyParentExists();

        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        final PrincipalKey creator =
            authInfo != null && authInfo.isAuthenticated() ? authInfo.getUser().getKey() : PrincipalKey.from( "user:system:admin" );

        final AccessControlList paramPerm = params.inheritPermissions() ? getPermissions( params.getParent() ) : params.getPermissions();
        final AccessControlList permissions =
            paramPerm == null || paramPerm.isEmpty() ? NodeDefaultAclFactory.create( creator ) : paramPerm;

        final Long manualOrderValue = resolvePotentialManualOrderValue();

        final Node.Builder nodeBuilder = Node.newNode().
            id( this.params.getNodeId() != null ? params.getNodeId() : new NodeId() ).
            createdTime( now ).
            modifiedTime( now ).
            creator( creator ).
            modifier( creator ).
            parent( params.getParent() ).
            name( NodeName.from( params.getName() ) ).
            data( params.getData() ).
            attachments( params.getAttachments() != null ? params.getAttachments() : Attachments.empty() ).
            indexConfigDocument( params.getIndexConfigDocument() ).
            hasChildren( false ).
            childOrder( params.getChildOrder() != null ? params.getChildOrder() : ChildOrder.defaultOrder() ).
            manualOrderValue( manualOrderValue ).
            permissions( permissions ).
            inheritPermissions( params.inheritPermissions() ).
            collection( params.getCollection() != null ? params.getCollection() : NodeCollection.DEFAULT_NODE_COLLECTION );

        final Node newNode = nodeBuilder.build();

        this.doStoreNode( newNode );

        return newNode;
    }

    private void verifyParentExists()
    {

        // FIX, add after CMS-4588 Make sure a parent exist before creating a node

      /*
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
        */
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


