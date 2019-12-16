package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.query;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.BaseTestBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompareExpressionBuilderTest
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
                                                                           List.of( ValueExpr.string( "myFirstValue" ),
                                                                                    ValueExpr.string( "mySecondValue" ) ) ) );
    }

    private void buildCompareExpr( final String fileName, final CompareExpr expr )
        throws Exception
    {
        final String expected = load( fileName );
        final String expression = CompareExpressionBuilder.build( expr, new SearchQueryFieldNameResolver() ).toString();

        assertEquals( cleanString( expected ), cleanString( expression ) );
    }
}
