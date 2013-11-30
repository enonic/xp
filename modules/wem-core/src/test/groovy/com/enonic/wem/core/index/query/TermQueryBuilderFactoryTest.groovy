package com.enonic.wem.core.index.query

import com.enonic.wem.query.expr.CompareExpr
import com.enonic.wem.query.expr.FieldExpr
import com.enonic.wem.query.expr.ValueExpr
import org.elasticsearch.index.query.QueryBuilder

class TermQueryBuilderFactoryTest extends BaseTestQueryBuilderFactory
{
    def "compare eq string"( )
    {
        given:
        def TermQueryBuilderFactory builder = new TermQueryBuilderFactory();
        def expected = this.getClass().getResource( "compare_eq_string.json" ).text

        when:
        final QueryBuilder query = builder.create( CompareExpr.eq( new FieldExpr( "myField" ), ValueExpr.string( "myValue" ) ) );

        then:
        cleanString( expected ) == cleanString( query.toString() )
    }

    def "compare eq number"( )
    {
        given:
        def TermQueryBuilderFactory builder = new TermQueryBuilderFactory();
        def expected = this.getClass().getResource( "compare_eq_number.json" ).text

        when:
        final QueryBuilder query = builder.create( CompareExpr.eq( new FieldExpr( "myField" ), ValueExpr.number( 1 ) ) );

        then:
        cleanString( expected ) == cleanString( query.toString() )
    }

    def "compare eq datetime"( )
    {
        given:
        def TermQueryBuilderFactory builder = new TermQueryBuilderFactory();
        def expected = this.getClass().getResource( "compare_eq_datetime.json" ).text

        when:
        final QueryBuilder query = builder.create( CompareExpr.eq( new FieldExpr( "myField" ), ValueExpr.dateTime( "2013-11-29T09:42:00" ) ) );

        then:
        cleanString( expected ) == cleanString( query.toString() )

    }

    def "compare eq geo point"( )
    {
        given:
        def TermQueryBuilderFactory builder = new TermQueryBuilderFactory();
        def expected = this.getClass().getResource( "compare_eq_geopoint.json" ).text

        when:
        final QueryBuilder query = builder.create( CompareExpr.eq( new FieldExpr( "myField" ), ValueExpr.geoPoint( "59.9127300,10.746090" ) ) );

        then:
        cleanString( expected ) == cleanString( query.toString() )
    }


}
