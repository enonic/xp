package com.enonic.wem.core.index.query

import com.enonic.wem.query.expr.CompareExpr
import com.enonic.wem.query.expr.FieldExpr
import com.enonic.wem.query.expr.ValueExpr
import org.elasticsearch.index.query.QueryBuilder

class RangeQueryBuilderFactoryTest extends BaseTestQueryBuilderFactory
{
    def "compare gt number"( )
    {
        given:
        def RangeQueryBuilderFactory builder = new RangeQueryBuilderFactory();
        def expected = this.getClass().getResource( "compare_gt_number.json" ).text

        when:
        final QueryBuilder query = builder.create( CompareExpr.gt( new FieldExpr( "myField" ), ValueExpr.number( 3L ) ) );

        then:
        cleanString( expected ) == cleanString( query.toString() )
    }

    def "compare gte number"( )
    {
        given:
        def RangeQueryBuilderFactory builder = new RangeQueryBuilderFactory();
        def expected = this.getClass().getResource( "compare_gte_number.json" ).text

        when:
        final QueryBuilder query = builder.create( CompareExpr.gte( new FieldExpr( "myField" ), ValueExpr.number( 3L ) ) );

        then:
        cleanString( expected ) == cleanString( query.toString() )
    }

    def "compare gt datetime"( )
    {
        given:
        def RangeQueryBuilderFactory builder = new RangeQueryBuilderFactory();
        def expected = this.getClass().getResource( "compare_gt_datetime.json" ).text

        when:
        final QueryBuilder query = builder.create( CompareExpr.gt( new FieldExpr( "myField" ), ValueExpr.dateTime( "2013-11-29T11:00:00" ) ) );

        then:
        cleanString( expected ) == cleanString( query.toString() )
    }

    def "compare gt string"( )
    {
        given:
        def RangeQueryBuilderFactory builder = new RangeQueryBuilderFactory();
        def expected = this.getClass().getResource( "compare_gt_string.json" ).text

        when:
        final QueryBuilder query = builder.create( CompareExpr.gt( new FieldExpr( "myField" ), ValueExpr.string( "myString" ) ) );

        then:
        cleanString( expected ) == cleanString( query.toString() )
    }
}
