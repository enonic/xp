package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyTree;

import static org.junit.jupiter.api.Assertions.assertThrows;

class StemmedQueryBuilderTest
    extends QueryBuilderTest
{
    @Test
    void simple()
        throws Exception
    {
        test( "simple" );
    }

    @Test
    void multiple_fields()
        throws Exception
    {
        test( "multiple_fields" );
    }

    @Test
    void operator()
        throws Exception
    {
        test( "operator" );
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

    @Test
    void invalid_language()
    {
        assertThrows( IllegalArgumentException.class, () -> test( "invalid_language" ) );
    }


    private void test( final String fileName )
        throws Exception
    {
        final String queryString = load( "stemmed/query/" + fileName + ".json" );

        final PropertyTree dslExpression = readJson( queryString );
        final QueryBuilder builder = new StemmedQueryBuilder( dslExpression.getSet( "stemmed" ) ).create();

        assertJson( "stemmed/result/" + fileName + ".json", builder.toString() );

    }

}
