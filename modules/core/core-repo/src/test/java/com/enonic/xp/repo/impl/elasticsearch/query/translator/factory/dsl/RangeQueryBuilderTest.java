package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyTree;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class RangeQueryBuilderTest
    extends QueryBuilderTest
{
    @Test
    public void null_value()
        throws Exception
    {
        assertThrows( RuntimeException.class, () -> test( "null_value" ) );
    }

    @Test
    public void empty_from()
        throws Exception
    {
        assertThrows( RuntimeException.class, () -> test( "empty_from" ) );
    }

    @Test
    public void empty_to()
        throws Exception
    {
        assertThrows( RuntimeException.class, () -> test( "empty_to" ) );
    }

    @Test
    public void from_date()
        throws Exception
    {
        test( "from_date" );
    }

    @Test
    public void from_number()
        throws Exception
    {
        test( "from_number" );
    }

    @Test
    public void from_string()
        throws Exception
    {
        test( "from_string" );
    }

    @Test
    public void from_string_include()
        throws Exception
    {
        test( "from_string_include" );
    }

    @Test
    public void to_date()
        throws Exception
    {
        test( "to_date" );
    }

    @Test
    public void to_number()
        throws Exception
    {
        test( "to_number" );
    }

    @Test
    public void to_string()
        throws Exception
    {
        test( "to_string" );
    }

    @Test
    public void to_string_include()
        throws Exception
    {
        test( "to_string_include" );
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
        final String queryString = load( "range/query/" + fileName + ".json" );

        final PropertyTree dslExpression = readJson( queryString );
        final QueryBuilder builder = new RangeQueryBuilder( dslExpression.getSet( "range" ) ).create();

        assertJson( "range/result/" + fileName + ".json", builder.toString() );
    }
}
