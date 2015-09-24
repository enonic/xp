package com.enonic.wem.repo.internal.index;

import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.google.common.collect.Lists;

import com.enonic.wem.repo.internal.elasticsearch.query.translator.SearchQueryFieldNameResolver;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.ValueExpr;

@RunWith(value = Parameterized.class)
public class IndexNodeIndexPathResolverTest
{
    private final String field;

    private final ValueExpr valueExpr;

    private final String resolvedFieldName;

    public IndexNodeIndexPathResolverTest( final String field, final ValueExpr valueExpr, final String resolvedFieldName )
    {
        this.field = field;
        this.valueExpr = valueExpr;
        this.resolvedFieldName = resolvedFieldName;
    }

    @Parameterized.Parameters(name = "{0}, {1} => {2}")
    public static Collection<Object[]> testParams()
    {
        final List<Object[]> list = Lists.newArrayList();
        list.add( paramsLine( "A", ValueExpr.string( "test" ), "a" ) );
        list.add( paramsLine( "A.b", ValueExpr.string( "test" ), "a.b" ) );
        list.add( paramsLine( "A.B.c", ValueExpr.string( "test" ), "a.b.c" ) );
        list.add( paramsLine( "A.b.c", ValueExpr.number( 1.0 ), "a.b.c._number" ) );
        list.add( paramsLine( "A.B.C", ValueExpr.number( 1L ), "a.b.c._number" ) );
        list.add( paramsLine( "A.B.C", ValueExpr.geoPoint( "80,80" ), "a.b.c._geopoint" ) );
        list.add( paramsLine( "A.B.C", ValueExpr.instant( "2013-08-01T10:00:00.000Z" ), "a.b.c._datetime" ) );
        return list;
    }

    private static Object[] paramsLine( final String field, final ValueExpr valueExpr, final String resolvedFieldName )
    {
        return new Object[]{field, valueExpr, resolvedFieldName};
    }

    @Test
    public void testResolve()
    {
        final String result = new SearchQueryFieldNameResolver().resolve( CompareExpr.eq( FieldExpr.from( this.field ), this.valueExpr ) );
        Assert.assertEquals( this.resolvedFieldName, result );
    }
}
