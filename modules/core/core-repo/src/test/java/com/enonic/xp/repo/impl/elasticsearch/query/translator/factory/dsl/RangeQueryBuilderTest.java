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
    public void empty_lt()
        throws Exception
    {
        assertThrows( RuntimeException.class, () -> test( "empty_lt" ) );
    }

    @Test
    public void empty_gt()
        throws Exception
    {
        assertThrows( RuntimeException.class, () -> test( "empty_gt" ) );
    }

    @Test
    public void gt_date()
        throws Exception
    {
        test( "gt_date" );
    }

    @Test
    public void gt_number()
        throws Exception
    {
        test( "gt_number" );
    }

    @Test
    public void gt_string()
        throws Exception
    {
        test( "gt_string" );
    }

    @Test
    public void gte()
        throws Exception
    {
        test( "gte" );
    }

    @Test
    public void lt_date()
        throws Exception
    {
        test( "lt_date" );
    }

    @Test
    public void lt_number()
        throws Exception
    {
        test( "lt_number" );
    }

    @Test
    public void lt_string()
        throws Exception
    {
        test( "lt_string" );
    }

    @Test
    public void lte()
        throws Exception
    {
        test( "lte" );
    }

    @Test
    public void boosted()
        throws Exception
    {
        test( "boosted" );
    }

    @Test
    public void diff_field_types()
        throws Exception
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
