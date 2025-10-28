package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyTree;

class NgramQueryBuilderTest
    extends QueryBuilderTest
{
    @Test
    void simple()
        throws Exception
    {
        test( "simple" );
    }

    @Test
    void operator()
        throws Exception
    {
        test( "operator" );
    }

    @Test
    void multiple_fields()
        throws Exception
    {
        test( "multiple_fields" );
    }

    @Test
    void empty_query()
        throws Exception
    {
        test( "empty_query" );
    }

    @Test
    void weighted()
        throws Exception
    {
        test( "weighted" );
    }

    @Test
    void boosted()
        throws Exception
    {
        test( "boosted" );
    }

    private void test( final String fileName )
        throws Exception
    {
        final String queryString = load( "ngram/query/" + fileName + ".json" );

        final PropertyTree dslExpression = readJson( queryString );
        final QueryBuilder builder = new NgramQueryBuilder( dslExpression.getSet( "ngram" ) ).create();

        assertJson( "ngram/result/" + fileName + ".json", builder.toString() );

    }

}
