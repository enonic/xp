package com.enonic.xp.repo.impl.node;

import java.util.Objects;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.search.result.SearchResult;

public class FindNodeIdsByParentCommand
    extends AbstractNodeCommand
{
    private final NodePath parentPath;

    private final NodeId parentId;

    private final Filters queryFilters;

    private final Integer size;

    private final Integer from;

    private final ChildOrder childOrder;

    private final boolean countOnly;

    private final boolean recursive;

    private FindNodeIdsByParentCommand( final Builder builder )
    {
        super( builder );
        parentPath = builder.parentPath;
        parentId = builder.parentId;
        queryFilters = Objects.requireNonNullElseGet( builder.queryFilters, Filters::empty );
        size = builder.size;
        from = builder.from;
        childOrder = builder.childOrder;
        countOnly = builder.countOnly;
        recursive = builder.recursive;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public FindNodesByParentResult execute()
    {
        NodePath parentPath = getParentPath();

        if ( parentPath == null )
        {
            return FindNodesByParentResult.create().nodeIds( NodeIds.empty() ).build();
        }

        final SearchResult result =
            this.nodeSearchService.query( createFindChildrenQuery( parentPath ), SingleRepoSearchSource.from( ContextAccessor.current() ) );

        return FindNodesByParentResult.create()
            .nodeIds( NodeIds.from( result.getIds() ) )
            .totalHits( result.getTotalHits() )
            .build();
    }

    private NodeQuery createFindChildrenQuery( final NodePath parentPath )
    {
        final ChildOrder order = resolveChildOrder( parentPath );

        final NodeQuery.Builder builder = NodeQuery.create()
            .addQueryFilters( queryFilters )
            .from( countOnly ? 0 : from )
            .size( countOnly ? 0 : size )
            .setOrderExpressions( order.getOrderExpressions() );

        if ( !recursive )
        {
            builder.parent( parentPath );
        }
        else
        {
            createParentFilter( parentPath, builder );
        }

        return builder.build();
    }

    private ChildOrder resolveChildOrder( final NodePath parentPath )
    {
        if ( this.childOrder == null || this.childOrder.isEmpty() )
        {
            final Node parentNode = NodeHelper.runAsAdmin( () -> doGetByPath( parentPath ) );
            return parentNode == null ? ChildOrder.defaultOrder() : parentNode.getChildOrder();
        }
        return this.childOrder;
    }

    private void createParentFilter( final NodePath parentPath, final NodeQuery.Builder builder )
    {
        if ( parentPath.isRoot() )
        {
            builder.query( QueryExpr.from( CompareExpr.neq( FieldExpr.from( NodeIndexPath.PATH ), ValueExpr.string( "/" ) ) ) );
        }
        else
        {
            final ValueExpr parentPathExpr = ValueExpr.string( parentPath + "/*" );
            builder.query( QueryExpr.from( CompareExpr.like( FieldExpr.from( NodeIndexPath.PATH ), parentPathExpr ) ) );
        }
    }

    private NodePath getParentPath()
    {
        NodePath parentPath = this.parentPath;

        if ( parentPath != null )
        {
            return parentPath;
        }

        Node parent = doGetById( parentId );

        if ( parent == null )
        {
            return null;
        }
        else
        {
            return parent.path();
        }
    }


    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodePath parentPath;

        private NodeId parentId;

        private Filters queryFilters;

        private Integer size = NodeSearchService.GET_ALL_SIZE_FLAG;

        private Integer from = 0;

        private ChildOrder childOrder;

        private boolean countOnly;

        private boolean recursive;

        public Builder()
        {
            super();
        }

        public Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public FindNodeIdsByParentCommand build()
        {
            this.validate();
            return new FindNodeIdsByParentCommand( this );
        }

        public Builder parentPath( final NodePath val )
        {
            parentPath = val;
            return this;
        }

        public Builder parentId( final NodeId val )
        {
            parentId = val;
            return this;
        }


        public Builder queryFilters( Filters val )
        {
            this.queryFilters = val;
            return this;
        }

        public Builder size( final Integer val )
        {
            size = val;
            return this;
        }

        public Builder from( final Integer val )
        {
            from = val;
            return this;
        }

        public Builder childOrder( final ChildOrder val )
        {
            childOrder = val;
            return this;
        }

        public Builder countOnly( final boolean val )
        {
            countOnly = val;
            return this;
        }

        public Builder recursive( final boolean val )
        {
            recursive = val;
            return this;
        }
    }
}
