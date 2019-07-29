package com.enonic.xp.repo.impl.elasticsearch.highlight;

import org.elasticsearch.search.highlight.HighlightBuilder;

import com.enonic.xp.query.highlight.HighlightQuery;
import com.enonic.xp.query.highlight.HighlightQueryField;
import com.enonic.xp.repo.impl.elasticsearch.query.ElasticHighlightQuery;
import com.enonic.xp.repo.impl.index.IndexFieldNameNormalizer;
import com.enonic.xp.repo.impl.index.IndexValueType;

public class ElasticHighlightQueryBuilderFactory
{

    public ElasticHighlightQuery create( final HighlightQuery highlightQuery )
    {
        if ( highlightQuery == null )
        {
            return ElasticHighlightQuery.empty();
        }

        ElasticHighlightQuery.Builder result = ElasticHighlightQuery.create();

        for ( HighlightQueryField highlightQueryField : highlightQuery.getFields() )
        {
            final String normalizedFieldName = IndexFieldNameNormalizer.normalize( highlightQueryField.getName() );
            final String normalizedFieldNameWithPostFix = normalizedFieldName + IndexValueType.INDEX_VALUE_TYPE_SEPARATOR + "*";
            final HighlightBuilder.Field rawHighlightField = new HighlightBuilder.Field( normalizedFieldName );
            final HighlightBuilder.Field analyzedHighlightField = new HighlightBuilder.Field( normalizedFieldNameWithPostFix );
            result.addField( rawHighlightField );
            result.addField( analyzedHighlightField );
        }

        return result.build();
    }
}
