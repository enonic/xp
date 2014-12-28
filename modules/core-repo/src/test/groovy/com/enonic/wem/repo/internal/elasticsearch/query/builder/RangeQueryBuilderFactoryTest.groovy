package com.enonic.wem.repo.internal.elasticsearch.query.builder

import com.enonic.wem.api.query.expr.CompareExpr
import com.enonic.wem.api.query.expr.FieldExpr
import com.enonic.wem.api.query.expr.ValueExpr
import org.elasticsearch.index.query.QueryBuilder

class RangeQueryBuilderFactoryTest
    extends BaseTestBuilderFactory
{
    def "compare gt number"()
    {
        given:
        def RangeQueryBuilderFactory builder = new RangeQueryBuilderFactory();
        def expected = this.getClass().getResource( "compare_gt_number.json" ).text

        when:
        final QueryBuilder query = builder.create( CompareExpr.gt( FieldExpr.from( "myField" ), ValueExpr.number( 3L ) ) );

        then:
        cleanString( expected ) == cleanString( query.toString() )
    }

    def "compare gte number"()
    {
        given:
        def RangeQueryBuilderFactory builder = new RangeQueryBuilderFactory();
        def expected = this.getClass().getResource( "compare_gte_number.json" ).text

        when:
        final QueryBuilder query = builder.create( CompareExpr.gte( FieldExpr.from( "myField" ), ValueExpr.number( 3L ) ) );

        then:
        cleanString( expected ) == cleanString( query.toString() )
    }

    def "compare gt instant"()
    {
        given:
        def RangeQueryBuilderFactory builder = new RangeQueryBuilderFactory();
        def expected = this.getClass().getResource( "compare_gt_datetime.json" ).text

        when:
        final QueryBuilder query = builder.create(
            CompareExpr.gt( FieldExpr.from( "myField" ), ValueExpr.instant( "2013-11-29T11:00:00.000Z" ) ) );

        then:
        cleanString( expected ) == cleanString( query.toString() )
    }

    def "compare gt string"()
    {
        given:
        def RangeQueryBuilderFactory builder = new RangeQueryBuilderFactory();
        def expected = this.getClass().getResource( "compare_gt_string.json" ).text

        when:
        final QueryBuilder query = builder.create( CompareExpr.gt( FieldExpr.from( "myField" ), ValueExpr.string( "myString" ) ) );

        then:
        cleanString( expected ) == cleanString( query.toString() )
    }
}
