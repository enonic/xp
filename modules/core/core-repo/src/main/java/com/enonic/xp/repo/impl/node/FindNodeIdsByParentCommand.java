package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.SearchMode;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.index.query.NodeQueryResult;

public class FindNodeIdsByParentCommand
    extends AbstractNodeCommand
{
    private final FindNodesByParentParams params;

    private FindNodeIdsByParentCommand( Builder builder )
    {
        super( builder );
        params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public NodeIds execute()
    {
        NodePath parentPath = getParentPath();

        if ( parentPath == null )
        {
            return NodeIds.empty();
        }

        final ChildOrder order = NodeChildOrderResolver.create( this ).
            nodePath( parentPath ).
            childOrder( params.getChildOrder() ).
            build().
            resolve();

        final NodeQueryResult nodeQueryResult =
            this.searchService.query( createFindChildrenQuery( parentPath, order ), InternalContext.from( ContextAccessor.current() ) );

        if ( nodeQueryResult.getHits() == 0 )
        {
            return NodeIds.empty();
        }

        return nodeQueryResult.getNodeIds();
    }

    private NodeQuery createFindChildrenQuery( final NodePath parentPath, final ChildOrder order )
    {
        final NodeQuery.Builder builder = NodeQuery.create().
            from( params.getFrom() ).
            size( params.getSize() ).
            searchMode( params.isCountOnly() ? SearchMode.COUNT : SearchMode.SCAN ).
            setOrderExpressions( order.getOrderExpressions() ).
            accurateScoring( true );

        if ( !params.isRecursive() )
        {
            builder.parent( parentPath );
        }
        else
        {
            builder.query( QueryExpr.from(
                CompareExpr.like( FieldExpr.from( NodeIndexPath.PARENT_PATH ), ValueExpr.string( parentPath.toString() + "*" ) ) ) );
        }

        return builder.build();
    }

    private NodePath getParentPath()
    {
        NodePath parentPath = params.getParentPath();

        if ( parentPath == null )
        {
            Node parent = GetNodeByIdCommand.create( this ).
                id( params.getParentId() ).
                build().
                execute();

            if ( parent == null )
            {
                parentPath = null;
            }
            else
            {
                parentPath = parent.path();
            }
        }
        return parentPath;
    }


    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private FindNodesByParentParams params;

        public Builder()
        {
            super();
        }

        public Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder params( FindNodesByParentParams params )
        {
            this.params = params;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.params );
        }

        public FindNodeIdsByParentCommand build()
        {
            this.validate();
            return new FindNodeIdsByParentCommand( this );
        }
    }
}
