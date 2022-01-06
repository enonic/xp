package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.util.JsonHelper;

public class FulltextQueryBuilderTest
    extends QueryBuilderTest
{
    @Test
    public void simple()
        throws Exception
    {
        test( "simple" );
    }

    @Test
    public void operator()
        throws Exception
    {
        test( "operator" );
    }

    @Test
    public void multiple_fields()
        throws Exception
    {
        test( "multiple_fields" );
    }

    @Test
    public void empty_query()
        throws Exception
    {
        test( "empty_query" );
    }

    @Test
    public void weighted()
        throws Exception
    {
        test( "weighted" );
    }

    @Test
    public void boosted()
        throws Exception
    {
        test( "boosted" );
    }

    private void test( final String fileName )
        throws Exception
    {
        final String queryString = load( "fulltext/query/" + fileName + ".json" );

        final PropertyTree dslExpression = JsonToPropertyTreeTranslator.translate( JsonHelper.from( queryString ) );
        final QueryBuilder builder = new FulltextQueryBuilder( dslExpression.getSet( "fulltext" ) ).create();

        assertJson( "fulltext/result/" + fileName + ".json", builder.toString() );
    }
}
