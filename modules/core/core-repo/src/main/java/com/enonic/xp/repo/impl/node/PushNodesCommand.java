package com.enonic.xp.repo.impl.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.OrderExpressions;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.storage.MoveNodeParams;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class PushNodesCommand
    extends AbstractNodeCommand
{
    private final Branch target;

    private final NodeIds ids;

    private final static Logger LOG = LoggerFactory.getLogger( PushNodesCommand.class );

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

        final NodeBranchEntries nodeBranchEntries = FindNodeBranchEntriesByIdCommand.create( this ).
            ids( ids ).
            orderExpressions( OrderExpressions.from( FieldOrderExpr.create( NodeIndexPath.PATH, OrderExpr.Direction.ASC ) ) ).
            build().
            execute();

        final PushNodesResult.Builder builder = PushNodesResult.create();

        for ( final NodeBranchEntry nodeBranchEntry : nodeBranchEntries )
        {
            final NodeComparison nodeComparison = CompareNodeCommand.create().
                nodeId( nodeBranchEntry.getNodeId() ).
                storageService( this.storageService ).
                target( this.target ).
                build().
                execute();

            // if ( !NodePermissionsResolver.userHasPermission( authInfo, Permission.PUBLISH, node ) )
            //  {
            //      builder.addFailed( node, PushNodesResult.Reason.ACCESS_DENIED );
            //      continue;
            //  }

            if ( nodeComparison.getCompareStatus() == CompareStatus.EQUAL )
            {
                builder.addSuccess( nodeBranchEntry );
                continue;
            }

            pushNode( context, builder, nodeBranchEntry );

            if ( nodeComparison.getCompareStatus() == CompareStatus.MOVED )
            {
                updateTargetChildrenMetaData( nodeBranchEntry, builder );
            }
        }

        RefreshCommand.create().
            refreshMode( RefreshMode.ALL ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();

        return builder.build();
    }

    private void pushNode( final Context context, final PushNodesResult.Builder builder, final NodeBranchEntry nodeBranchEntry )
    {
        final NodeVersionId nodeVersionId =
            this.storageService.getBranchNodeVersion( nodeBranchEntry.getNodeId(), InternalContext.from( context ) ).getVersionId();

        if ( nodeVersionId == null )
        {
            throw new NodeNotFoundException( "Node version for node with id '" + nodeBranchEntry.getNodeId() + "' not found" );
        }

        if ( !targetParentExists( nodeBranchEntry.getNodePath(), context ) )
        {
            builder.addFailed( nodeBranchEntry, PushNodesResult.Reason.PARENT_NOT_FOUND );
        }
        else
        {
            doPushNode( context, nodeBranchEntry, nodeVersionId );
            builder.addSuccess( nodeBranchEntry );
        }
    }

    private void updateTargetChildrenMetaData( final NodeBranchEntry nodeBranchEntry, PushNodesResult.Builder resultBuilder )
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

        final NodeIds children = FindNodeIdsByParentCommand.create( this ).
            params( FindNodesByParentParams.create().
                parentPath( nodeBranchEntry.getNodePath() ).
                childOrder( ChildOrder.from( NodeIndexPath.PATH + " asc" ) ).
                build() ).
            build().
            execute();

        final NodeBranchEntries childEntries =
            this.storageService.getBranchNodeVersions( children, InternalContext.from( ContextAccessor.current() ) );

        for ( final NodeBranchEntry child : childEntries )
        {

            final NodeBranchEntry targetNodeEntry =
                this.storageService.getBranchNodeVersion( child.getNodeId(), InternalContext.from( targetContext ) );

            if ( targetNodeEntry != null )
            {
                final Node childNode = GetNodeByIdCommand.create( this ).
                    id( child.getNodeId() ).
                    build().
                    execute();

                this.storageService.move( MoveNodeParams.create().
                    updateMetadataOnly( true ).
                    node( childNode ).
                    build(), InternalContext.from( targetContext ) );

                resultBuilder.addSuccess( child );

                updateTargetChildrenMetaData( child, resultBuilder );
            }
        }
    }

    private void doPushNode( final Context context, final NodeBranchEntry node, final NodeVersionId nodeVersionId )
    {
        this.storageService.publish( node, nodeVersionId, InternalContext.create( context ).
            branch( this.target ).
            build(), context.getBranch() );
    }

    private boolean targetParentExists( final NodePath nodePath, final Context currentContext )
    {
        if ( nodePath.isRoot() || nodePath.getParentPath().equals( NodePath.ROOT ) )
        {
            return true;
        }

        final Context targetContext = createTargetContext( currentContext );

        return targetContext.callWith( () -> CheckNodeExistsCommand.create( this ).
            nodePath( nodePath.getParentPath() ).
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
