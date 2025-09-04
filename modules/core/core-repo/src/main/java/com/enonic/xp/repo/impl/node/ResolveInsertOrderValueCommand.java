package com.enonic.xp.repo.impl.node;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.search.result.SearchResult;

public class ResolveInsertOrderValueCommand
    extends AbstractNodeCommand
{
    private final NodePath parentPath;

    private final boolean lower;

    private final Long referenceValue;

    private ResolveInsertOrderValueCommand( final Builder builder )
    {
        super( builder );
        parentPath = builder.parentPath;
        referenceValue = builder.referenceValue;
        lower = builder.lower;
    }

    public long execute()
    {
        final Long manualOrderValue = NodeHelper.runAsAdmin( this::getManualOrderValue );
        if ( manualOrderValue == null )
        {
            return NodeManualOrderValueResolver.first();
        }

        return lower
            ? NodeManualOrderValueResolver.after( manualOrderValue )
            : ( referenceValue == null
                ? NodeManualOrderValueResolver.before( manualOrderValue )
                : NodeManualOrderValueResolver.between( referenceValue, manualOrderValue ) );
    }

    private Long getManualOrderValue()
    {
        final ChildOrder childOrder = lower ? ChildOrder.reverseManualOrder() : ChildOrder.manualOrder();

        refresh( RefreshMode.SEARCH );
        final NodeQuery.Builder query =
            NodeQuery.create().size( 1 ).parent( parentPath ).setOrderExpressions( childOrder.getOrderExpressions() );

        if ( referenceValue != null )
        {
            query.query( QueryExpr.from(
                CompareExpr.gt( FieldExpr.from( NodeIndexPath.MANUAL_ORDER_VALUE ), ValueExpr.number( referenceValue ) ) ) );
        }

        final SearchResult searchResult =
            this.nodeSearchService.query( query.build(), SingleRepoSearchSource.from( ContextAccessor.current() ) );
        if ( searchResult.isEmpty() )
        {
            return null;
        }
        else
        {
            final Node node = doGetById( NodeId.from( searchResult.getHits().getFirst().getId() ) );
            final Long manualOrderValue = node.getManualOrderValue();
            if ( manualOrderValue == null )
            {
                throw new IllegalStateException( "Node with id [" + node.id() + "] missing manual order value" );
            }

            return manualOrderValue;
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

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodePath parentPath;

        private boolean lower;

        private Long referenceValue;

        private Builder()
        {
        }

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder parentPath( final NodePath val )
        {
            parentPath = val;
            return this;
        }

        public Builder lower( final boolean val )
        {
            lower = val;
            return this;
        }

        public Builder referenceValue( final Long val )
        {
            referenceValue = val;
            return this;
        }

        public ResolveInsertOrderValueCommand build()
        {
            return new ResolveInsertOrderValueCommand( this );
        }
    }
}
