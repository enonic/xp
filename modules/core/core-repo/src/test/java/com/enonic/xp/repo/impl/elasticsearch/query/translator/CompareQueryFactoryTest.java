package com.enonic.xp.repo.impl.elasticsearch.query.translator;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.CompareQueryBuilderFactory;

public class CompareQueryFactoryTest
    extends BaseTestBuilderFactory
{
    @Test
    public void buildCompareExpr()
        throws Exception
    {
        buildCompareExpr( "compare_eq_string.json", CompareExpr.eq( FieldExpr.from( "myField" ), ValueExpr.string( "myValue" ) ) );
        buildCompareExpr( "compare_eq_number.json", CompareExpr.eq( FieldExpr.from( "myField" ), ValueExpr.number( 1.0 ) ) );
        buildCompareExpr( "compare_eq_datetime.json",
                          CompareExpr.eq( FieldExpr.from( "myField" ), ValueExpr.instant( "2013-11-29T09:42:00.000Z" ) ) );
        buildCompareExpr( "compare_eq_geopoint.json",
                          CompareExpr.eq( FieldExpr.from( "myField" ), ValueExpr.geoPoint( "59.9127300,10.746090" ) ) );
        buildCompareExpr( "compare_neq_string.json", CompareExpr.neq( FieldExpr.from( "myField" ), ValueExpr.string( "myValue" ) ) );
        buildCompareExpr( "compare_not_like_string.json",
                          CompareExpr.notLike( FieldExpr.from( "myField" ), ValueExpr.string( "myValue" ) ) );
        buildCompareExpr( "compare_not_in_string.json", CompareExpr.notIn( FieldExpr.from( "myField" ),
                                                                           Lists.newArrayList( ValueExpr.string( "myFirstValue" ),
                                                                                               ValueExpr.string( "mySecondValue" ) ) ) );
    }

    private void buildCompareExpr( final String fileName, final CompareExpr expr )
        throws Exception
    {
        final String expected = load( fileName );
        final String expression = new CompareQueryBuilderFactory( new SearchQueryFieldNameResolver() ).create( expr ).toString();

        Assert.assertEquals( cleanString( expected ), cleanString( expression ) );
    }
}
