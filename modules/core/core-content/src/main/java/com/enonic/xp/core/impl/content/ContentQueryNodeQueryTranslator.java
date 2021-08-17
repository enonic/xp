package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentIndexPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.filter.BooleanFilter;
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

        builder.query( contentQuery.getQueryExpr() )
            .from( contentQuery.getFrom() )
            .size( contentQuery.getSize() )
            .addAggregationQueries( contentQuery.getAggregationQueries() )
            .addQueryFilters( contentQuery.getQueryFilters() )
            .addQueryFilter( contentCollectionFilter )
            .highlight( contentQuery.getHighlight() );

        if ( !contentQuery.isIncludeArchive() )
        {
            builder.addQueryFilter( BooleanFilter.create()
                                        .mustNot( ValueFilter.create()
                                                      .fieldName( NodeIndexPath.PATH.getPath() )
                                                      .addValue( ValueFactory.newString( "/content/__archive__/*" ) )
                                                      .build() )
                                        .mustNot( ValueFilter.create()
                                                      .fieldName( NodeIndexPath.PATH.getPath() )
                                                      .addValue( ValueFactory.newString( "/content/__archive__" ) )
                                                      .build() )
                                        .build() );
        }

        processContentTypesNames( contentQuery, builder );
        processReferenceIds( contentQuery, builder );

        return builder;
    }

    private static void processContentTypesNames( final ContentQuery contentQuery, final NodeQuery.Builder builder )
    {
        final ContentTypeNames contentTypeNames = contentQuery.getContentTypes();

        if ( contentTypeNames != null && contentTypeNames.isNotEmpty() )
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

        if ( contentIds != null && contentIds.isNotEmpty() )
        {
            final IdFilter.Builder contentTypeFilterBuilder =
                IdFilter.create().fieldName( ContentIndexPath.ID.getPath() ).values( contentIds.asStrings() ).setCache( true );

            builder.addQueryFilter( contentTypeFilterBuilder.build() );
        }
    }
}
