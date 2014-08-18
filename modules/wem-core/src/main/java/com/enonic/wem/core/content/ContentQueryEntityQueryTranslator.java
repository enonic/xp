package com.enonic.wem.core.content;

import com.enonic.wem.api.content.query.ContentQuery;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.entity.query.EntityQuery;
import com.enonic.wem.api.query.filter.Filter;
import com.enonic.wem.api.query.filter.GenericValueFilter;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.core.index.IndexConstants;

public class ContentQueryEntityQueryTranslator
{
    public static EntityQuery translate( final ContentQuery contentQuery )
    {
        final EntityQuery.Builder entityQueryBuilder = EntityQuery.newEntityQuery();

        doTranslateEntityQueryProperties( contentQuery, entityQueryBuilder );

        return entityQueryBuilder.build();
    }


    protected static void doTranslateEntityQueryProperties( final ContentQuery contentQuery, final EntityQuery.Builder builder )
    {
        builder.query( contentQuery.getQueryExpr() ).
            from( contentQuery.getFrom() ).
            size( contentQuery.getSize() ).
            addAggregationQueries( contentQuery.getAggregationQueries() ).
            addQueryFilters( contentQuery.getQueryFilters() );

        final ContentTypeNames contentTypeNames = contentQuery.getContentTypes();

        if ( contentTypeNames != null && contentTypeNames.isNotEmpty() )
        {
            final GenericValueFilter.Builder contentTypeFilterBuilder = Filter.newValueQueryFilter().fieldName( "contentType" );

            for ( final ContentTypeName contentTypeName : contentTypeNames )
            {
                contentTypeFilterBuilder.add( Value.newString( contentTypeName.toString() ) );
            }

            builder.addQueryFilter( contentTypeFilterBuilder.build() );
        }

        addCollectionFilter( builder );
    }

    private static void addCollectionFilter( final EntityQuery.Builder entityQueryBuilder )
    {
        entityQueryBuilder.addQueryFilter( Filter.newValueQueryFilter().
            fieldName( IndexConstants.COLLECTION_FIELD ).
            add( Value.newString( IndexConstants.CONTENT_COLLECTION_NAME ) ).
            build() );
    }


}
