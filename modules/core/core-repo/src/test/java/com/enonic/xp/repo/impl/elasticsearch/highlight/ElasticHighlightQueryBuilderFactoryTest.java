package com.enonic.xp.repo.impl.elasticsearch.highlight;

import java.util.List;
import java.util.stream.Collectors;

import org.elasticsearch.search.highlight.HighlightBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.query.highlight.HighlightQuery;
import com.enonic.xp.query.highlight.HighlightQueryProperty;
import com.enonic.xp.query.highlight.HighlightQuerySettings;
import com.enonic.xp.query.highlight.constants.Encoder;
import com.enonic.xp.query.highlight.constants.Fragmenter;
import com.enonic.xp.query.highlight.constants.Order;
import com.enonic.xp.query.highlight.constants.TagsSchema;
import com.enonic.xp.repo.impl.elasticsearch.query.ElasticHighlightQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ElasticHighlightQueryBuilderFactoryTest
{
    private ElasticHighlightQueryBuilderFactory highlightQueryBuilderFactory;

    @BeforeEach
    void init()
    {
        highlightQueryBuilderFactory = new ElasticHighlightQueryBuilderFactory();
    }

    @Test
    void create()
    {
        final HighlightQuery query = HighlightQuery.create().
            property( HighlightQueryProperty.create( "propertyToHighlight" ).build() ).
            build();

        final ElasticHighlightQuery elasticHighlightQuery = highlightQueryBuilderFactory.create( query );

        assertNotNull( elasticHighlightQuery );
        assertEquals( 2, elasticHighlightQuery.getFields().size() );

        final List<String> names =
            elasticHighlightQuery.getFields().stream().map( HighlightBuilder.Field::name ).collect( Collectors.toList() );
        assertTrue( names.containsAll( List.of( "propertytohighlight._*", "propertytohighlight" ) ) );
    }

    @Test
    void create_with_settings()
    {
        final HighlightQuery query = HighlightQuery.create().
            property( HighlightQueryProperty.create( "propertyToHighlight" ).build() ).
            settings( HighlightQuerySettings.create().
                encoder( Encoder.HTML ).
                fragmenter( Fragmenter.SIMPLE ).
                fragmentSize( 1 ).
                noMatchSize( 2 ).
                numOfFragments( 3 ).
                order( Order.SCORE ).
                addPreTags( List.of( "<a>", "<b>" ) ).
                addPostTags( List.of( "<c>", "<d>" ) ).
                requireFieldMatch( true ).
                tagsSchema( TagsSchema.STYLED ).
                build() ).
            build();

        final ElasticHighlightQuery elasticHighlightQuery = highlightQueryBuilderFactory.create( query );

        assertNotNull( elasticHighlightQuery );
        assertEquals( 2, elasticHighlightQuery.getFields().size() );
        assertEquals( Encoder.HTML, elasticHighlightQuery.getEncoder() );
        assertEquals( 1, (int) elasticHighlightQuery.getFragmentSize() );
        assertEquals( 2, (int) elasticHighlightQuery.getNoMatchSize() );
        assertEquals( 3, (int) elasticHighlightQuery.getNumOfFragments() );
        assertEquals( List.of( "<a>", "<b>" ), elasticHighlightQuery.getPreTags() );
        assertEquals( List.of( "<c>", "<d>" ), elasticHighlightQuery.getPostTags() );
        assertEquals( true, elasticHighlightQuery.getRequireFieldMatch() );
        assertEquals( TagsSchema.STYLED, elasticHighlightQuery.getTagsSchema() );
    }

    @Test
    void create_null()
    {
        final ElasticHighlightQuery elasticHighlightQuery = highlightQueryBuilderFactory.create( null );
        assertEquals( ElasticHighlightQuery.empty(), elasticHighlightQuery );
    }
}
