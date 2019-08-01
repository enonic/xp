package com.enonic.xp.repo.impl.elasticsearch.highlight;

import org.elasticsearch.search.highlight.HighlightBuilder;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.query.highlight.HighlightQuery;
import com.enonic.xp.query.highlight.HighlightQueryField;
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

        for ( HighlightQueryField highlightQueryField : highlightQuery.getFields() )
        {
            final String normalizedFieldName = IndexFieldNameNormalizer.normalize( highlightQueryField.getName() );
            final String normalizedFieldNameWithPostFix = normalizedFieldName + IndexValueType.INDEX_VALUE_TYPE_SEPARATOR + "_*";
            final HighlightBuilder.Field rawHighlightField = createField( normalizedFieldName, highlightQueryField );
            final HighlightBuilder.Field analyzedHighlightField = createField( normalizedFieldNameWithPostFix, highlightQueryField );

            result.addField( rawHighlightField );
            result.addField( analyzedHighlightField );
        }

        return result.build();
    }

    private HighlightBuilder.Field createField( final String name, final HighlightQueryField field )
    {
        final HighlightBuilder.Field builder = new HighlightBuilder.Field( name );

        final Fragmenter fragmenter = field.getFragmenter();
        final Integer fragmentSize = field.getFragmentSize();
        final Integer noMatchSize = field.getNoMatchSize();
        final Integer numOfFragments = field.getNumOfFragments();
        final Order order = field.getOrder();
        final ImmutableList<String> preTags = field.getPreTags();
        final ImmutableList<String> postTags = field.getPostTags();
        final Boolean requireFieldMatch = field.getRequireFieldMatch();

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
            builder.preTags( preTags.toArray( new String[preTags.size()]) );
        }
        if (postTags != null && !postTags.isEmpty()) {
            builder.postTags( postTags.toArray( new String[postTags.size()]) );
        }
        if (requireFieldMatch != null) {
            builder.requireFieldMatch( requireFieldMatch );
        }

        return builder;
    }
}
