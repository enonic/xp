package com.enonic.wem.repo.internal.elasticsearch.query.builder

import com.enonic.wem.api.query.expr.CompareExpr
import com.enonic.wem.api.query.expr.FieldExpr
import com.enonic.wem.api.query.expr.ValueExpr
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
