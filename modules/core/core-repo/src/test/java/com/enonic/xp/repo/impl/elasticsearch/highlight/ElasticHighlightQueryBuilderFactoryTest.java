package com.enonic.xp.repo.impl.elasticsearch.highlight;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.query.highlight.HighlightQuery;
import com.enonic.xp.query.highlight.HighlightQueryField;
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
        Assert.assertEquals( "fieldtohighlight._*", elasticHighlightQuery.getFields().asList().get( 0 ).name() );
        Assert.assertEquals( "fieldtohighlight", elasticHighlightQuery.getFields().asList().get( 1 ).name() );
    }

    @Test
    public void create_null()
    {
        final ElasticHighlightQuery elasticHighlightQuery = highlightQueryBuilderFactory.create( null );
        Assert.assertEquals( ElasticHighlightQuery.empty(), elasticHighlightQuery );
    }
}
