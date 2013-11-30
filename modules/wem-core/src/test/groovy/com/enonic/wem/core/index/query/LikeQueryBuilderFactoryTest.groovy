package com.enonic.wem.core.index.query

import com.enonic.wem.query.expr.CompareExpr
import com.enonic.wem.query.expr.FieldExpr
import com.enonic.wem.query.expr.ValueExpr
import org.elasticsearch.index.query.QueryBuilder

class LikeQueryBuilderFactoryTest extends BaseTestQueryBuilderFactory
{

    def "compare like string"( )
    {
        given:
        def LikeQueryBuilderFactory builder = new LikeQueryBuilderFactory();
        def expected = this.getClass().getResource( "compare_like_string.json" ).text

        when:
        final QueryBuilder query = builder.create( CompareExpr.like( new FieldExpr( "myField" ), ValueExpr.string( "myValue" ) ) );

        then:
        cleanString( expected ) == cleanString( query.toString() )
    }


}
