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
    public EntityQuery translate( final ContentQuery contentQuery )
    {
        final EntityQuery.Builder entityQueryBuilder = EntityQuery.newQuery().
            query( contentQuery.getQueryExpr() ).
            from( contentQuery.getFrom() ).
            size( contentQuery.getSize() );

        final ContentTypeNames contentTypeNames = contentQuery.getContentTypes();

        if ( contentTypeNames != null && contentTypeNames.isNotEmpty() )
        {
            final GenericValueFilter.Builder contentTypeFilterBuilder = Filter.newValueQueryFilter().fieldName( "contentTypeName" );

            for ( final ContentTypeName contentTypeName : contentTypeNames )
            {
                contentTypeFilterBuilder.add( new Value.String( contentTypeName.toString() ) );
            }

            entityQueryBuilder.addQueryFilter( contentTypeFilterBuilder.build() );
        }

        addCollectionFilter( entityQueryBuilder );

        return entityQueryBuilder.build();
    }

    private void addCollectionFilter( final EntityQuery.Builder entityQueryBuilder )
    {
        entityQueryBuilder.addQueryFilter( Filter.newValueQueryFilter().
            fieldName( IndexConstants.COLLECTION_FIELD ).
            add( new Value.String( IndexConstants.CONTENT_COLLECTION_NAME ) ).
            build() );
    }


}
