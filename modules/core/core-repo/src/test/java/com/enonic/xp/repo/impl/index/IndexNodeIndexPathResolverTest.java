package com.enonic.xp.repo.impl.index;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IndexNodeIndexPathResolverTest
{

    public static Stream<Arguments> testParams()
    {
        return Stream.of(
        Arguments.of( "A", ValueExpr.string( "test" ), "a" ) ,
        Arguments.of( "A.b", ValueExpr.string( "test" ), "a.b" ) ,
        Arguments.of( "A.B.c", ValueExpr.string( "test" ), "a.b.c" ) ,
        Arguments.of( "A.b.c", ValueExpr.number( 1.0 ), "a.b.c._number" ) ,
        Arguments.of( "A.B.C", ValueExpr.number( 1L ), "a.b.c._number" ) ,
        Arguments.of( "A.B.C", ValueExpr.geoPoint( "80,80" ), "a.b.c._geopoint" ) ,
        Arguments.of( "A.B.C", ValueExpr.instant( "2013-08-01T10:00:00.000Z" ), "a.b.c._datetime" )
        );
    }


    @ParameterizedTest
    @MethodSource("testParams")
    public void testResolve(final String field, final ValueExpr valueExpr, final String resolvedFieldName)
    {
        final String result = SearchQueryFieldNameResolver.INSTANCE.resolve( CompareExpr.eq( FieldExpr.from( field ), valueExpr ) );
        assertEquals( resolvedFieldName, result );
    }
}
