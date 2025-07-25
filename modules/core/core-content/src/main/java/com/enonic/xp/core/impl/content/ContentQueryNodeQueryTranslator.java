package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentIndexPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.ConstraintExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.LogicalExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.query.filter.IdFilter;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;

class ContentQueryNodeQueryTranslator
{
    public static NodeQuery.Builder translate( final ContentQuery contentQuery )
    {
        final NodeQuery.Builder builder = NodeQuery.create();

        final ValueFilter contentCollectionFilter = ValueFilter.create()
            .fieldName( NodeIndexPath.NODE_TYPE.getPath() )
            .addValue( ValueFactory.newString( ContentConstants.CONTENT_NODE_COLLECTION.getName() ) )
            .build();

        builder.query( buildNodeQueryExpr( contentQuery ) )
            .from( contentQuery.getFrom() )
            .size( contentQuery.getSize() )
            .addAggregationQueries( contentQuery.getAggregationQueries() )
            .addQueryFilters( contentQuery.getQueryFilters() )
            .addQueryFilter( contentCollectionFilter )
            .highlight( contentQuery.getHighlight() );

        processContentTypesNames( contentQuery, builder );
        processReferenceIds( contentQuery, builder );

        return builder;
    }

    private static void processContentTypesNames( final ContentQuery contentQuery, final NodeQuery.Builder builder )
    {
        final ContentTypeNames contentTypeNames = contentQuery.getContentTypes();

        if ( contentTypeNames != null && !contentTypeNames.isEmpty() )
        {
            final ValueFilter.Builder contentTypeFilterBuilder =
                ValueFilter.create().fieldName( ContentPropertyNames.TYPE ).setCache( true );

            for ( final ContentTypeName contentTypeName : contentTypeNames )
            {
                contentTypeFilterBuilder.addValue( ValueFactory.newString( contentTypeName.toString() ) );
            }

            builder.addQueryFilter( contentTypeFilterBuilder.build() );
        }
    }

    private static void processReferenceIds( final ContentQuery contentQuery, final NodeQuery.Builder builder )
    {
        final ContentIds contentIds = contentQuery.getFilterContentIds();

        if ( contentIds != null && !contentIds.isEmpty() )
        {
            final IdFilter.Builder contentTypeFilterBuilder = IdFilter.create()
                .fieldName( ContentIndexPath.ID.getPath() )
                .values( contentIds )
                .setCache( true );

            builder.addQueryFilter( contentTypeFilterBuilder.build() );
        }
    }

    private static QueryExpr buildNodeQueryExpr( final ContentQuery contentQuery )
    {
        final QueryExpr queryExpr = contentQuery.getQueryExpr();
        final CompareExpr contentPathRootExpr =
            CompareExpr.like( FieldExpr.from( "_path" ), ValueExpr.string( ContentNodeHelper.getContentRoot() + "/*" ) );

        if ( queryExpr != null )
        {
            final ConstraintExpr newExpr =
                queryExpr.getConstraint() != null ? LogicalExpr.and( queryExpr.getConstraint(), contentPathRootExpr ) : contentPathRootExpr;

            return QueryExpr.from( newExpr, queryExpr.getOrderList() );

        }

        return QueryExpr.from( contentPathRootExpr );
    }
}
