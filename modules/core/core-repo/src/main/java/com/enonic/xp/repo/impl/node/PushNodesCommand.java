package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.OrderExpressions;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.storage.MoveNodeParams;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class PushNodesCommand
    extends AbstractNodeCommand
{
    private final Branch target;

    private final NodeIds ids;

    private PushNodesCommand( final Builder builder )
    {
        super( builder );
        this.target = builder.target;
        this.ids = builder.ids;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public PushNodesResult execute()
    {
        final Context context = ContextAccessor.current();
        final AuthenticationInfo authInfo = context.getAuthInfo();

        final Nodes nodes = FindNodesByIdsCommand.create( this ).
            ids( ids ).
            orderExpressions( OrderExpressions.from( FieldOrderExpr.create( NodeIndexPath.PATH, OrderExpr.Direction.ASC ) ) ).
            searchService( this.searchService ).
            build().
            execute();

        final PushNodesResult.Builder builder = PushNodesResult.create();

        for ( final Node node : nodes )
        {
            final NodeComparison nodeComparison = CompareNodeCommand.create().
                nodeId( node.id() ).
                storageService( this.storageService ).
                target( this.target ).
                build().
                execute();

            if ( !NodePermissionsResolver.userHasPermission( authInfo, Permission.PUBLISH, node ) )
            {
                builder.addFailed( node, PushNodesResult.Reason.ACCESS_DENIED );
                continue;
            }

            if ( nodeComparison.getCompareStatus() == CompareStatus.EQUAL )
            {
                builder.addSuccess( node );
                continue;
            }

            final NodeVersionId nodeVersionId =
                this.storageService.getBranchNodeVersion( node.id(), InternalContext.from( context ) ).getVersionId();

            if ( nodeVersionId == null )
            {
                throw new NodeNotFoundException( "Node version for node with id '" + node.id() + "' not found" );
            }

            if ( !targetParentExists( node, context ) )
            {
                builder.addFailed( node, PushNodesResult.Reason.PARENT_NOT_FOUND );
            }
            else
            {
                doPushNode( context, node, nodeVersionId );
                builder.addSuccess( node );
            }

            if ( nodeComparison.getCompareStatus() == CompareStatus.MOVED )
            {
                updateTargetChildrenMetaData( node, builder );
            }
        }

        RefreshCommand.create().
            refreshMode( RefreshMode.ALL ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();

        return builder.build();
    }

    private void updateTargetChildrenMetaData( final Node node, PushNodesResult.Builder resultBuilder )
    {
        // So, we have moved a node, and the pushed it.
        // The children of the pushed node are all changed, and every equal node on target must get updated meta-data.
        // If the child node does not exist in target, just ignore it, no moving necessary

        final Context context = ContextAccessor.current();

        final Context targetContext = ContextBuilder.create().
            authInfo( context.getAuthInfo() ).
            branch( this.target ).
            repositoryId( context.getRepositoryId() ).
            build();

        final FindNodesByParentResult result = FindNodesByParentCommand.create( this ).
            params( FindNodesByParentParams.create().
                parentPath( node.path() ).
                childOrder( ChildOrder.from( NodeIndexPath.PATH + " asc" ) ).
                build() ).
            build().
            execute();

        for ( final Node child : result.getNodes() )
        {
            final Node nodeInTarget = targetContext.callWith( () -> GetNodeByIdCommand.create( this ).
                id( child.id() ).
                build().
                execute() );

            if ( nodeInTarget != null )
            {
                this.storageService.move( MoveNodeParams.create().
                    updateMetadataOnly( true ).
                    node( child ).
                    build(), InternalContext.from( targetContext ) );

                resultBuilder.addSuccess( child );

                updateTargetChildrenMetaData( child, resultBuilder );
            }
        }
    }

    private void doPushNode( final Context context, final Node node, final NodeVersionId nodeVersionId )
    {
        this.storageService.updateVersion( node, nodeVersionId, InternalContext.create( context ).
            branch( this.target ).
            build() );
    }

    private boolean targetParentExists( final Node node, final Context currentContext )
    {
        if ( node.isRoot() || node.parentPath().equals( NodePath.ROOT ) )
        {
            return true;
        }

        final Context targetContext = createTargetContext( currentContext );

        return targetContext.callWith( () -> CheckNodeExistsCommand.create( this ).
            nodePath( node.parentPath() ).
            build().
            execute() );
    }

    private Context createTargetContext( final Context currentContext )
    {
        final ContextBuilder targetContext = ContextBuilder.create().
            repositoryId( currentContext.getRepositoryId() ).
            branch( target );

        if ( currentContext.getAuthInfo() != null )
        {
            targetContext.authInfo( currentContext.getAuthInfo() );
        }

        return targetContext.build();
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private Branch target;

        private NodeIds ids;

        Builder()
        {
            super();
        }

        public Builder target( final Branch target )
        {
            this.target = target;
            return this;
        }

        public Builder ids( final NodeIds nodeIds )
        {
            this.ids = nodeIds;
            return this;
        }

        public PushNodesCommand build()
        {
            validate();
            return new PushNodesCommand( this );
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( target );
            Preconditions.checkNotNull( ids );
        }
    }
}
