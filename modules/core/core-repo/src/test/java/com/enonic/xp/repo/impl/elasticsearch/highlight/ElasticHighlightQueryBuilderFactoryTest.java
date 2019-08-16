package com.enonic.xp.repo.impl.elasticsearch.highlight;

import java.util.List;
import java.util.stream.Collectors;

import org.elasticsearch.search.highlight.HighlightBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.query.highlight.HighlightFieldSettings;
import com.enonic.xp.query.highlight.HighlightQuery;
import com.enonic.xp.query.highlight.HighlightQueryField;
import com.enonic.xp.query.highlight.HighlightQuerySettings;
import com.enonic.xp.query.highlight.constants.Encoder;
import com.enonic.xp.query.highlight.constants.Fragmenter;
import com.enonic.xp.query.highlight.constants.Order;
import com.enonic.xp.query.highlight.constants.TagsSchema;
import com.enonic.xp.repo.impl.elasticsearch.query.ElasticHighlightQuery;

public class ElasticHighlightQueryBuilderFactoryTest
{
    private ElasticHighlightQueryBuilderFactory highlightQueryBuilderFactory;

    @Before
    public void init()
    {
        highlightQueryBuilderFactory = new ElasticHighlightQueryBuilderFactory();
    }

    @Test
    public void create()
    {
        final HighlightQuery query = HighlightQuery.create().
            field( HighlightQueryField.create( "fieldToHighlight" ).build() ).
            build();

        final ElasticHighlightQuery elasticHighlightQuery = highlightQueryBuilderFactory.create( query );

        Assert.assertNotNull( elasticHighlightQuery );
        Assert.assertEquals( 2, elasticHighlightQuery.getFields().size() );

        final List<String> names =
            elasticHighlightQuery.getFields().stream().map( HighlightBuilder.Field::name ).collect( Collectors.toList() );
        Assert.assertTrue( names.containsAll( List.of( "fieldtohighlight._*", "fieldtohighlight" ) ) );
    }

    @Test
    public void create_with_settings()
    {
        final HighlightQuery query = HighlightQuery.create().
            field( HighlightQueryField.create( "fieldToHighlight" ).build() ).
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

        Assert.assertNotNull( elasticHighlightQuery );
        Assert.assertEquals( 2, elasticHighlightQuery.getFields().size() );
        Assert.assertEquals( Encoder.HTML, elasticHighlightQuery.getEncoder() );
        Assert.assertEquals( 1, (int) elasticHighlightQuery.getFragmentSize() );
        Assert.assertEquals( 2, (int) elasticHighlightQuery.getNoMatchSize() );
        Assert.assertEquals( 3, (int) elasticHighlightQuery.getNumOfFragments() );
        Assert.assertEquals( List.of( "<a>", "<b>" ), elasticHighlightQuery.getPreTags() );
        Assert.assertEquals( List.of( "<c>", "<d>" ), elasticHighlightQuery.getPostTags() );
        Assert.assertEquals( true, elasticHighlightQuery.getRequireFieldMatch() );
        Assert.assertEquals( TagsSchema.STYLED, elasticHighlightQuery.getTagsSchema() );
    }

    @Test
    public void create_null()
    {
        final ElasticHighlightQuery elasticHighlightQuery = highlightQueryBuilderFactory.create( null );
        Assert.assertEquals( ElasticHighlightQuery.empty(), elasticHighlightQuery );
    }
}
