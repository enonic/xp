package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyTree;

import static org.junit.jupiter.api.Assertions.assertThrows;


class InQueryBuilderTest
    extends QueryBuilderTest
{

    @Test
    void null_value()
    {
        assertThrows( IllegalArgumentException.class, () -> test( "null_value" ) );
    }

    @Test
    void empty()
    {
        assertThrows( IllegalArgumentException.class, () -> test( "empty" ) );
    }

    @Test
    void one()
        throws Exception
    {
        test( "one" );
    }

    @Test
    void multiple()
        throws Exception
    {
        test( "multiple" );
    }

    @Test
    void with_geopoint()
    {
        assertThrows( IllegalArgumentException.class, () -> test( "with_geopoint" ) );
    }

    @Test
    void with_number()
        throws Exception
    {
        test( "with_number" );
    }

    @Test
    void with_dateTime()
        throws Exception
    {
        test( "with_dateTime" );
    }

    @Test
    void with_date()
        throws Exception
    {
        test( "with_date" );
    }

    @Test
    void with_localDateTime()
        throws Exception
    {
        test( "with_localDateTime" );
    }

    @Test
    void with_mixed_date()
        throws Exception
    {
        test( "with_mixed_date" );
    }

    @Test
    void with_time()
        throws Exception
    {
        test( "with_time" );
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
        final String queryString = load( "in/query/" + fileName + ".json" );

        final PropertyTree dslExpression = readJson( queryString );
        final QueryBuilder builder = new InQueryBuilder( dslExpression.getSet( "in" ) ).create();

        assertJson( "in/result/" + fileName + ".json", builder.toString() );

    }

}
