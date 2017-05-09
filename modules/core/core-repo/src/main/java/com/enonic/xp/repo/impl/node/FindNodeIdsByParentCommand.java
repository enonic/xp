package com.enonic.xp.repo.impl.node;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.SearchMode;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.index.query.NodeQueryResult;
import com.enonic.xp.repo.impl.search.NodeSearchService;

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
        queryFilters = builder.queryFilters == null ? Filters.from() : builder.queryFilters;
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
            return FindNodesByParentResult.empty();
        }

        final ChildOrder order = NodeChildOrderResolver.create( this ).
            nodePath( parentPath ).
            childOrder( childOrder ).
            build().
            resolve();

        final NodeQueryResult nodeQueryResult = this.nodeSearchService.query( createFindChildrenQuery( parentPath, order ),
                                                                              SingleRepoSearchSource.from( ContextAccessor.current() ) );

        if ( nodeQueryResult.getHits() == 0 )
        {
            return FindNodesByParentResult.empty();
        }

        return FindNodesByParentResult.create().
            nodeIds( nodeQueryResult.getNodeIds() ).
            totalHits( nodeQueryResult.getTotalHits() ).
            hits( nodeQueryResult.getHits() ).
            build();
    }

    private NodeQuery createFindChildrenQuery( final NodePath parentPath, final ChildOrder order )
    {
        final NodeQuery.Builder builder = NodeQuery.create().
            addQueryFilters( queryFilters ).
            from( from ).
            size( size ).
            searchMode( countOnly ? SearchMode.COUNT : SearchMode.SEARCH ).
            setOrderExpressions( order.getOrderExpressions() ).
            accurateScoring( true );

        if ( !recursive )
        {
            builder.parent( parentPath );
        }
        else
        {
            final ValueExpr parentPathExpr = ValueExpr.string( parentPath.toString() + "/*" );
            builder.query( QueryExpr.from( CompareExpr.like( FieldExpr.from( NodeIndexPath.PATH ), parentPathExpr ) ) );
        }

        return builder.build();
    }

    private NodePath getParentPath()
    {
        NodePath parentPath = this.parentPath;

        if ( parentPath == null )
        {
            Node parent = GetNodeByIdCommand.create( this ).
                id( parentId ).
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
        private NodePath parentPath;

        private NodeId parentId;

        private Filters queryFilters;

        private Integer size = NodeSearchService.GET_ALL_SIZE_FLAG;

        private Integer from = 0;

        private ChildOrder childOrder;

        private boolean countOnly = false;

        private boolean recursive = false;

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
