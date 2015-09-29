package com.enonic.wem.repo.internal.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.elasticsearch.query.translator.builder.AclFilterBuilderFactory;
import com.enonic.wem.repo.internal.index.query.NodeQueryResult;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.query.expr.OrderExpressions;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.query.filter.ValueFilter;

public class FindNodesByIdsCommand
    extends AbstractNodeCommand
{
    private final NodeIds ids;

    private final OrderExpressions orderExpressions;

    private FindNodesByIdsCommand( final Builder builder )
    {
        super( builder );
        this.ids = builder.ids;
        this.orderExpressions = builder.orderExpressions;
    }

    public Nodes execute()
    {
        final Context context = ContextAccessor.current();

        final NodeQueryResult result = this.searchService.search( NodeQuery.create().
            addQueryFilters( Filters.create().
                add( ValueFilter.create().
                    fieldName( NodeIndexPath.ID.getPath() ).
                    addValues( this.ids.getAsStrings() ).
                    build() ).
                build() ).
            size( ids.getSize() ).
            addQueryFilter( AclFilterBuilderFactory.create( context.getAuthInfo().getPrincipals() ) ).
            setOrderExpressions( this.orderExpressions ).
            build(), InternalContext.from( ContextAccessor.current() ) );

        final NodeIds nodeIds = result.getNodeIds();

        return this.storageService.get( nodeIds, InternalContext.from( context ) );
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
        private NodeIds ids;

        private OrderExpressions orderExpressions = DEFAULT_ORDER_EXPRESSIONS;

        private Builder()
        {
            super();
        }

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder ids( NodeIds ids )
        {
            this.ids = ids;
            return this;
        }

        public Builder orderExpressions( final OrderExpressions orderExpressions )
        {
            this.orderExpressions = orderExpressions;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.ids );
        }

        public FindNodesByIdsCommand build()
        {
            this.validate();
            return new FindNodesByIdsCommand( this );
        }
    }
}
