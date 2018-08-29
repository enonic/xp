package com.enonic.xp.admin.impl.rest.resource.content.query;

import java.util.stream.Collectors;

import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.ConstraintExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.LogicalExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;

public class ContentQueryWithChildren
{
    private ContentIds contentsIds;

    private ContentPaths contentsPaths;

    private ChildOrder order;

    private int size = 0;

    private int from = 0;

    private ContentService contentService;

    private ContentQueryWithChildren( Builder builder )
    {
        this.contentsIds = builder.contentsIds.build();
        this.contentsPaths = builder.contentsPaths.build();
        this.order = builder.order;
        this.size = builder.size;
        this.from = builder.from;
        this.contentService = builder.contentService;
    }

    private QueryExpr constructExprToFindChildren()
    {
        final ContentPaths contentsPaths = this.getPaths();

        final FieldExpr fieldExpr = FieldExpr.from( "_path" );

        ConstraintExpr expr = null;

        for ( ContentPath contentPath : contentsPaths )
        {
            ConstraintExpr likeExpr = CompareExpr.like( fieldExpr, ValueExpr.string( "/content/" + contentPath.asRelative() + "/*" ) );
            expr = expr != null ? LogicalExpr.or( expr, likeExpr ) : likeExpr;
        }

        expr = LogicalExpr.and( expr, CompareExpr.notIn( fieldExpr, contentsPaths.stream().
            map( contentPath -> ValueExpr.string( "/content/" + contentPath.asRelative() ) ).collect( Collectors.toList() ) ) );

        return QueryExpr.from( expr, new FieldOrderExpr( fieldExpr, OrderExpr.Direction.ASC ) );
    }

    private QueryExpr constructExprToFindOrdered()
    {
        final ContentPaths contentsPaths = this.getPaths();

        final FieldExpr fieldExpr = FieldExpr.from( "_path" );

        final CompareExpr compareExpr = CompareExpr.in( fieldExpr, contentsPaths.stream().
            map( contentPath -> ValueExpr.string( "/content/" + contentPath.asRelative() ) ).collect( Collectors.toList() ) );

        return QueryExpr.from( compareExpr, this.order != null ? this.order.getOrderExpressions() : null );
    }

    public ContentPaths getPaths()
    {
        if ( !this.contentsPaths.isEmpty() )
        {
            return this.contentsPaths;
        }
        else if ( !this.contentsIds.isEmpty() )
        {
            return contentService.getByIds( new GetContentByIdsParams( contentsIds ) ).getPaths();
        }
        return ContentPaths.empty();
    }

    public FindContentIdsByQueryResult find()
    {
        if ( contentsPaths.getSize() == 0 && contentsIds.getSize() == 0 )
        {
            return FindContentIdsByQueryResult.empty();
        }
        final QueryExpr expr = constructExprToFindChildren();
        final ContentQuery query = ContentQuery.create().from( this.from ).size( this.size ).queryExpr( expr ).build();
        return this.contentService.find( query );
    }

    public FindContentIdsByQueryResult findOrdered()
    {
        if ( contentsPaths.getSize() == 0 && contentsIds.getSize() == 0 )
        {
            return FindContentIdsByQueryResult.empty();
        }
        final QueryExpr expr = constructExprToFindOrdered();
        final ContentQuery query = ContentQuery.create().from( this.from ).size( this.size ).queryExpr( expr ).build();
        return this.contentService.find( query );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ContentIds.Builder contentsIds = ContentIds.create();

        private ContentPaths.Builder contentsPaths = ContentPaths.create();

        private ChildOrder order;

        private int size = 0;

        private int from = 0;

        private ContentService contentService;

        private Builder()
        {
        }

        public Builder contentsIds( ContentIds contentsIds )
        {
            this.contentsIds.addAll( contentsIds );
            return this;
        }

        public Builder contentsPaths( ContentPaths contentsPaths )
        {
            this.contentsPaths.addAll( contentsPaths );
            return this;
        }

        public Builder order( ChildOrder order )
        {
            this.order = order;
            return this;
        }

        public Builder size( int size )
        {
            this.size = size;
            return this;
        }

        public Builder from( int from )
        {
            this.from = from;
            return this;
        }

        public Builder contentService( ContentService contentService )
        {
            this.contentService = contentService;
            return this;
        }

        public ContentQueryWithChildren build()
        {
            return new ContentQueryWithChildren( this );
        }
    }
}
