package com.enonic.wem.repo.internal.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.CompareStatus;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.context.ContextBuilder;
import com.enonic.wem.api.node.FindNodesByParentParams;
import com.enonic.wem.api.node.FindNodesByParentResult;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeComparison;
import com.enonic.wem.api.node.NodeIds;
import com.enonic.wem.api.node.NodeIndexPath;
import com.enonic.wem.api.node.NodeNotFoundException;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeVersionId;
import com.enonic.wem.api.node.Nodes;
import com.enonic.wem.api.node.PushNodesResult;
import com.enonic.wem.api.query.expr.FieldOrderExpr;
import com.enonic.wem.api.query.expr.OrderExpr;
import com.enonic.wem.api.query.expr.OrderExpressions;
import com.enonic.wem.api.branch.Branch;
import com.enonic.wem.repo.internal.index.IndexContext;
import com.enonic.wem.repo.internal.branch.StoreBranchDocument;
import com.enonic.wem.repo.internal.branch.BranchContext;

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

        final Nodes nodes =
            doGetByIds( ids, OrderExpressions.from( FieldOrderExpr.create( NodeIndexPath.PATH, OrderExpr.Direction.ASC ) ), false );

        final PushNodesResult.Builder builder = PushNodesResult.create();

        for ( final Node node : nodes )
        {
            final NodeComparison nodeComparison = CompareNodeCommand.create().
                nodeId( node.id() ).
                versionService( this.versionService ).
                branchService( this.branchService ).
                target( this.target ).
                build().
                execute();

            if ( nodeComparison.getCompareStatus().getStatus().equals( CompareStatus.Status.EQUAL ) )
            {
                builder.addSuccess( node );
            }

            final NodeVersionId nodeVersionId = this.queryService.get( node.id(), IndexContext.from( context ) );

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

            if ( nodeComparison.getCompareStatus().getStatus().equals( CompareStatus.Status.MOVED ) )
            {
                updateNodeChildrenWithNewMetadata( node );
            }
        }

        return builder.build();
    }

    private void updateNodeChildrenWithNewMetadata( final Node node )
    {
        final FindNodesByParentResult result = doFindNodesByParent( FindNodesByParentParams.create().
            parentPath( node.path() ).
            build() );

        final Context context = ContextAccessor.current();

        for ( final Node child : result.getNodes() )
        {
            ContextBuilder.create().
                authInfo( context.getAuthInfo() ).
                branch( this.target ).
                repositoryId( context.getRepositoryId() ).
                build().runWith( () -> updateNodeMetadata( child ) );

            updateNodeChildrenWithNewMetadata( child );
        }
    }

    private void doPushNode( final Context context, final Node node, final NodeVersionId nodeVersionId )
    {
        this.branchService.store( StoreBranchDocument.create().
            nodeVersionId( nodeVersionId ).
            node( node ).
            build(), BranchContext.from( this.target, context.getRepositoryId() ) );

        this.indexService.store( node, nodeVersionId, IndexContext.create().
            branch( this.target ).
            repositoryId( context.getRepositoryId() ).
            authInfo( context.getAuthInfo() ).
            build() );
    }

    boolean targetParentExists( final Node node, final Context currentContext )
    {
        if ( node.isRoot() || node.parentPath().equals( NodePath.ROOT ) )
        {
            return true;
        }

        final Context targetContext = createTargetContext( currentContext );

        final Node targetParent = targetContext.callWith( () -> doGetByPath( node.parentPath(), false ) );

        if ( targetParent == null )
        {
            return false;
        }

        return true;
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

        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( target );
            Preconditions.checkNotNull( ids );
        }
    }
}
