package com.enonic.wem.repo.internal.elasticsearch.query.builder;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.ValueExpr;

public class RangeQueryBuilderFactoryTest
    extends BaseTestBuilderFactory
{
    @Test
    public void compare_gt_number()
        throws Exception
    {
        final String expected = load( "compare_gt_number.json" );

        final QueryBuilder query = RangeQueryBuilderFactory.create( CompareExpr.gt( FieldExpr.from( "myField" ), ValueExpr.number( 3L ) ) );

        Assert.assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }

    @Test
    public void compare_gte_number()
        throws Exception
    {
        final String expected = load( "compare_gte_number.json" );

        final QueryBuilder query =
            RangeQueryBuilderFactory.create( CompareExpr.gte( FieldExpr.from( "myField" ), ValueExpr.number( 3L ) ) );

        Assert.assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }

    @Test
    public void compare_gte_instant()
        throws Exception
    {
        final String expected = load( "compare_gt_datetime.json" );

        final QueryBuilder query = RangeQueryBuilderFactory.create(
            CompareExpr.gt( FieldExpr.from( "myField" ), ValueExpr.instant( "2013-11-29T11:00:00.000Z" ) ) );

        Assert.assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }

    @Test
    public void compare_gt_string()
        throws Exception
    {
        final String expected = load( "compare_gt_string.json" );

        final QueryBuilder query =
            RangeQueryBuilderFactory.create( CompareExpr.gt( FieldExpr.from( "myField" ), ValueExpr.string( "myString" ) ) );

        Assert.assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }
}

