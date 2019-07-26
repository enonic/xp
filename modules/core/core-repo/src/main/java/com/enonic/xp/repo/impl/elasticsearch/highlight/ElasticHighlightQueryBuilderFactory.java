package com.enonic.xp.repo.impl.elasticsearch.highlight;

import org.elasticsearch.search.highlight.HighlightBuilder;

import com.enonic.xp.query.highlight.HighlightQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.ElasticHighlightQuery;

public class ElasticHighlightQueryBuilderFactory
{

    public ElasticHighlightQuery create( final HighlightQuery highlightQuery )
    {
        if ( highlightQuery == null )
        {
            return ElasticHighlightQuery.empty();
        }

        ElasticHighlightQuery.Builder result = ElasticHighlightQuery.create();

        highlightQuery.getFields().forEach( highlightQueryField -> {
            HighlightBuilder.Field field = new HighlightBuilder.Field( highlightQueryField.getName() );
            result.addField( field );
        } );

        return result.build();
    }
}
