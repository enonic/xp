package com.enonic.wem.repo.internal.entity;

import java.time.Instant;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.node.Attachments;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.FindNodesByParentParams;
import com.enonic.wem.api.node.FindNodesByParentResult;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeAlreadyExistException;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeName;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.acl.AccessControlList;

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

        final PrincipalKey creator = PrincipalKey.from( "user:system:admin" );

        final AccessControlList paramsAcl = params.getAccessControlList();
        final AccessControlList acl = paramsAcl == null || paramsAcl.isEmpty() ? NodeDefaultAclFactory.create( creator ) : paramsAcl;
        // calculate effective permissions based on parent acl
        final AccessControlList effectiveAcl = params.inheritPermissions()
            ? acl.getEffective( getAccessControlList( params.getParent() ) )
            : acl.getEffective( AccessControlList.empty() );

        final Long manualOrderValue = resolvePotentialManualOrderValue();

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
            manualOrderValue( manualOrderValue ).
            accessControlList( acl ).
            effectiveAcl( effectiveAcl );

        final Node newNode = nodeBuilder.build();

        this.doStoreNode( newNode );

        return newNode;
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
            final FindNodesByParentResult findNodesByParentResult = doFindNodesByParent( FindNodesByParentParams.create().
                parentPath( parentNode.path() ).
                childOrder( ChildOrder.manualOrder() ).
                size( 1 ).
                build() );

            if ( findNodesByParentResult.isEmpty() )
            {
                return NodeOrderValueResolver.START_ORDER_VALUE;
            }
            else
            {
                final Node first = findNodesByParentResult.getNodes().first();

                if ( first.getManualOrderValue() == null )
                {
                    throw new IllegalArgumentException( "Expected that node " + first +
                                                            " should have manualOrderValue since parent childOrder = manualOrderValue, but value was null" );
                }

                return first.getManualOrderValue() + NodeOrderValueResolver.ORDER_SPACE;
            }
        }

        return null;
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


