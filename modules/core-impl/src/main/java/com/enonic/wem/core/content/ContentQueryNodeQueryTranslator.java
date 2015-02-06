package com.enonic.wem.core.content;

import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentPropertyNames;
import com.enonic.wem.api.content.query.ContentQuery;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.node.NodeIndexPath;
import com.enonic.wem.api.node.NodeQuery;
import com.enonic.wem.api.query.filter.ValueFilter;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;

class ContentQueryNodeQueryTranslator
{
    public static NodeQuery translate( final ContentQuery contentQuery )
    {
        final NodeQuery.Builder builder = NodeQuery.create();

        doTranslate( contentQuery, builder );

        return builder.build();
    }

    private static void doTranslate( final ContentQuery contentQuery, final NodeQuery.Builder builder )
    {
        final ValueFilter contentCollectionFilter = ValueFilter.create().
            fieldName( NodeIndexPath.NODE_TYPE.getPath() ).
            addValue( Value.newString( ContentConstants.CONTENT_NODE_COLLECTION.getName() ) ).
            build();

        builder.query( contentQuery.getQueryExpr() ).
            from( contentQuery.getFrom() ).
            size( contentQuery.getSize() ).
            addAggregationQueries( contentQuery.getAggregationQueries() ).
            addQueryFilters( contentQuery.getQueryFilters() ).
            addQueryFilter( contentCollectionFilter );

        processContentTypesNames( contentQuery, builder );
    }

    private static void processContentTypesNames( final ContentQuery contentQuery, final NodeQuery.Builder builder )
    {
        final ContentTypeNames contentTypeNames = contentQuery.getContentTypes();

        if ( contentTypeNames != null && contentTypeNames.isNotEmpty() )
        {
            final ValueFilter.Builder contentTypeFilterBuilder = ValueFilter.create().
                fieldName( ContentPropertyNames.TYPE ).
                setCache( true );

            for ( final ContentTypeName contentTypeName : contentTypeNames )
            {
                contentTypeFilterBuilder.addValue( Value.newString( contentTypeName.toString() ) );
            }

            builder.addQueryFilter( contentTypeFilterBuilder.build() );
        }
    }


}
