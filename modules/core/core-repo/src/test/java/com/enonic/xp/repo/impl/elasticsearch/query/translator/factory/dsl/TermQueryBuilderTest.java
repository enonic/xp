package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyTree;

import static org.junit.jupiter.api.Assertions.assertThrows;

class TermQueryBuilderTest
    extends QueryBuilderTest
{
    @Test
    void simple()
        throws Exception
    {
        test( "simple" );
    }

    @Test
    void number()
        throws Exception
    {
        test( "number" );
    }

    @Test
    void test_boolean()
        throws Exception
    {
        test( "boolean" );
    }

    @Test
    void boosted()
        throws Exception
    {
        test( "boosted" );
    }

    @Test
    void empty_value()
        throws Exception
    {
        test( "empty_value" );
    }

    @Test
    void null_value()
        throws Exception
    {
        test( "null_value" );
    }

    @Test
    void datetime_as_number()
        throws Exception
    {
        test( "datetime_as_number" );
    }

    @Test
    void invalid_datetime()
    {
        assertThrows( RuntimeException.class, () -> test( "invalid_datetime" ) );
    }

    @Test
    void geo_point()
    {
        assertThrows( IllegalArgumentException.class, () -> test( "geo_point" ) );
    }

    private void test( final String fileName )
        throws Exception
    {
        final String queryString = load( "term/query/" + fileName + ".json" );

        final PropertyTree dslExpression = readJson( queryString );
        final QueryBuilder builder = new TermQueryBuilder( dslExpression.getSet( "term" ) ).create();

        assertJson( "term/result/" + fileName + ".json", builder.toString() );

    }

}
