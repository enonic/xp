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
        return new HighlightBuilder.Field( name ).
            fragmenter( field.getFragmenter() != null ? field.getFragmenter().value() : null ).
            fragmentSize( field.getFragmentSize() ).
            noMatchSize( field.getNoMatchSize() ).
            numOfFragments( field.getNumOfFragments() ).
            order( field.getOrder() != null ? field.getOrder().value() : null ).
            preTags( (String[]) field.getPreTags().toArray( new String[field.getPreTags().size()] ) ).
            postTags( (String[]) field.getPostTags().toArray( new String[field.getPostTags().size()] ) ).
            requireFieldMatch( field.getRequireFieldMatch() );
    }
}
