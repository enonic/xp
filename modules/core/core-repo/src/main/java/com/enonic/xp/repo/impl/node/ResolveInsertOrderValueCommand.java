package com.enonic.xp.repo.impl.node;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.search.result.SearchHit;
import com.enonic.xp.repo.impl.search.result.SearchResult;

public class ResolveInsertOrderValueCommand
    extends AbstractNodeCommand
{
    private final NodePath parentPath;


    private ResolveInsertOrderValueCommand( final Builder builder )
    {
        super( builder );
        parentPath = builder.parentPath;
    }

    public long insert( final boolean last )
    {
        final Long manualOrderValue = NodeHelper.runAsAdmin(
            () -> this.getManualOrderValue( last ? ChildOrder.reverseManualOrder() : ChildOrder.manualOrder(), null ) );
        if ( manualOrderValue == null )
        {
            return NodeManualOrderValueResolver.first();
        }
        else
        {
            return last ? NodeManualOrderValueResolver.after( manualOrderValue ) : NodeManualOrderValueResolver.before( manualOrderValue );
        }
    }

    public long reorder( final Long before, final Long current )
    {
        final Long manualOrderValue = NodeHelper.runAsAdmin( () -> this.getManualOrderValue( ChildOrder.reverseManualOrder(), before ) );

        if ( manualOrderValue == null )
        {
            return NodeManualOrderValueResolver.before( before );
        }
        else
        {
            return before == null
                ? NodeManualOrderValueResolver.after( manualOrderValue )
                : ( manualOrderValue.equals( current ) ? current : NodeManualOrderValueResolver.between( before, manualOrderValue ) );
        }
    }

    private Long getManualOrderValue( ChildOrder childOrder, Long referenceValue )
    {
        final NodeQuery.Builder query =
            NodeQuery.create().size( 1 ).parent( parentPath ).setOrderExpressions( childOrder.getOrderExpressions() );

        if ( referenceValue != null )
        {
            query.query( QueryExpr.from(
                CompareExpr.gt( FieldExpr.from( NodeIndexPath.MANUAL_ORDER_VALUE ), ValueExpr.number( referenceValue ) ) ) );
        }

        refresh( RefreshMode.SEARCH );
        final SearchResult searchResult =
            this.nodeSearchService.query( query.build(), ReturnFields.from( NodeIndexPath.MANUAL_ORDER_VALUE ),
                                          SingleRepoSearchSource.from( ContextAccessor.current() ) );
        if ( searchResult.isEmpty() )
        {
            return null;
        }
        else
        {
            final SearchHit hit = searchResult.getHits().getFirst();
            return hit.getReturnValues()
                .getOptional( NodeIndexPath.MANUAL_ORDER_VALUE )
                .map( Object::toString )
                .map( Long::valueOf )
                .orElseThrow(
                    () -> new IllegalStateException( String.format( "Node with id [%s] missing manual order value", hit.getId() ) ) );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodePath parentPath;

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

        public ResolveInsertOrderValueCommand build()
        {
            return new ResolveInsertOrderValueCommand( this );
        }
    }
}
