package com.enonic.xp.repo.impl.elasticsearch.highlight;

import java.util.List;

import org.elasticsearch.search.highlight.HighlightBuilder;

import com.enonic.xp.query.highlight.HighlightQuery;
import com.enonic.xp.query.highlight.HighlightQueryProperty;
import com.enonic.xp.query.highlight.constants.Fragmenter;
import com.enonic.xp.query.highlight.constants.Order;
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

        ElasticHighlightQuery.Builder result = ElasticHighlightQuery.create().
            settings( highlightQuery.getSettings() );

        for ( HighlightQueryProperty highlightQueryProperty : highlightQuery.getProperties() )
        {
            final String normalizedFieldName = IndexFieldNameNormalizer.normalize( highlightQueryProperty.getName() );
            final String normalizedFieldNameWithPostFix = normalizedFieldName + IndexValueType.INDEX_VALUE_TYPE_SEPARATOR + "_*";
            final HighlightBuilder.Field rawHighlightField = createField( normalizedFieldName, highlightQueryProperty );
            final HighlightBuilder.Field analyzedHighlightField = createField( normalizedFieldNameWithPostFix, highlightQueryProperty );

            result.addField( rawHighlightField );
            result.addField( analyzedHighlightField );
        }

        return result.build();
    }

    private HighlightBuilder.Field createField( final String name, final HighlightQueryProperty property )
    {
        final HighlightBuilder.Field builder = new HighlightBuilder.Field( name );

        final Fragmenter fragmenter = property.getFragmenter();
        final Integer fragmentSize = property.getFragmentSize();
        final Integer noMatchSize = property.getNoMatchSize();
        final Integer numOfFragments = property.getNumOfFragments();
        final Order order = property.getOrder();
        final List<String> preTags = property.getPreTags();
        final List<String> postTags = property.getPostTags();
        final Boolean requireFieldMatch = property.getRequireFieldMatch();

        if (fragmenter != null) {
            builder.fragmenter( fragmenter.value() );
        }
        if (fragmentSize != null) {
            builder.fragmentSize( fragmentSize );
        }
        if (noMatchSize != null) {
            builder.noMatchSize( noMatchSize );
        }
        if (numOfFragments != null) {
            builder.numOfFragments( numOfFragments );
        }
        if (order != null) {
            builder.order( order.value() );
        }
        if (preTags != null && !preTags.isEmpty()) {
            builder.preTags( preTags.toArray( new String[0] ) );
        }
        if (postTags != null && !postTags.isEmpty()) {
            builder.postTags( postTags.toArray( new String[0] ) );
        }
        if (requireFieldMatch != null) {
            builder.requireFieldMatch( requireFieldMatch );
        }

        return builder;
    }
}
