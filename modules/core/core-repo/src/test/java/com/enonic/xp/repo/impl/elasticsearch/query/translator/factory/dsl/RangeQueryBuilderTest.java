package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyTree;

import static org.junit.jupiter.api.Assertions.assertThrows;

class RangeQueryBuilderTest
    extends QueryBuilderTest
{
    @Test
    void null_value()
    {
        assertThrows( RuntimeException.class, () -> test( "null_value" ) );
    }

    @Test
    void empty_lt()
    {
        assertThrows( RuntimeException.class, () -> test( "empty_lt" ) );
    }

    @Test
    void empty_gt()
    {
        assertThrows( RuntimeException.class, () -> test( "empty_gt" ) );
    }

    @Test
    void gt_date()
        throws Exception
    {
        test( "gt_date" );
    }

    @Test
    void gt_number()
        throws Exception
    {
        test( "gt_number" );
    }

    @Test
    void gt_string()
        throws Exception
    {
        test( "gt_string" );
    }

    @Test
    void gte()
        throws Exception
    {
        test( "gte" );
    }

    @Test
    void lt_date()
        throws Exception
    {
        test( "lt_date" );
    }

    @Test
    void lt_number()
        throws Exception
    {
        test( "lt_number" );
    }

    @Test
    void lt_string()
        throws Exception
    {
        test( "lt_string" );
    }

    @Test
    void lte()
        throws Exception
    {
        test( "lte" );
    }

    @Test
    void boosted()
        throws Exception
    {
        test( "boosted" );
    }

    @Test
    void diff_field_types()
    {
        assertThrows( RuntimeException.class, () -> test( "diff_field_types" ) );
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
