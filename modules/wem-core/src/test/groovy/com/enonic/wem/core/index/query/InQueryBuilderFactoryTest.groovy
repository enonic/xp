package com.enonic.wem.core.index.query

import com.enonic.wem.api.query.expr.CompareExpr
import com.enonic.wem.api.query.expr.FieldExpr
import com.enonic.wem.api.query.expr.ValueExpr
import com.enonic.wem.core.index.query.builder.InQueryBuilderFactory
import org.elasticsearch.index.query.QueryBuilder

class InQueryBuilderFactoryTest
        extends BaseTestBuilderFactory
{
    def "compare in string"()
    {
        given:
        def InQueryBuilderFactory builder = new InQueryBuilderFactory();
        def expected = this.getClass().getResource( "compare_in_string.json" ).text

        when:
        final QueryBuilder query = builder.create(
                CompareExpr.in( new FieldExpr( "myField" ), [ValueExpr.string( "myFirstValue" ), ValueExpr.string( "mySecondValue" )] ) );

        then:
        cleanString( expected ) == cleanString( query.toString() )
    }


}
