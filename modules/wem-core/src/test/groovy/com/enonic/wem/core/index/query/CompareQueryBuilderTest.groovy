package com.enonic.wem.core.index.query

import com.enonic.wem.query.expr.CompareExpr
import com.enonic.wem.query.expr.FieldExpr
import com.enonic.wem.query.expr.ValueExpr
import org.elasticsearch.common.Strings
import org.elasticsearch.index.query.QueryBuilder
import org.joda.time.DateTimeZone
import spock.lang.Specification

class CompareQueryBuilderTest extends Specification
{
    private static final String LINE_BREAK = System.getProperty( "line.separator" )

    private static DateTimeZone origDefault = DateTimeZone.getDefault();

    def setupSpec( )
    {
        DateTimeZone.setDefault( DateTimeZone.UTC );
    }

    def cleanupSpec( )
    {
        DateTimeZone.setDefault( origDefault );
    }


    def "compare eq string"( )
    {
        given:
        def CompareQueryBuilder builder = new CompareQueryBuilder();
        def expected = this.getClass().getResource( "compare_eq_string.json" ).text

        when:
        final QueryBuilder query = builder.build( CompareExpr.eq( new FieldExpr( "myString" ), ValueExpr.string( "myValue" ) ) );

        then:
        cleanString( expected ) == cleanString( query.toString() )
    }

    def "compare eq number"( )
    {
        given:
        def CompareQueryBuilder builder = new CompareQueryBuilder();
        def expected = this.getClass().getResource( "compare_eq_number.json" ).text

        when:
        final QueryBuilder query = builder.build( CompareExpr.eq( new FieldExpr( "myNumber" ), ValueExpr.number( 1 ) ) );

        then:
        cleanString( expected ) == cleanString( query.toString() )
    }

    def "compare eq datetime"( )
    {
        given:
        def CompareQueryBuilder builder = new CompareQueryBuilder();
        def expected = this.getClass().getResource( "compare_eq_datetime.json" ).text

        when:
        final QueryBuilder query = builder.build( CompareExpr.eq( new FieldExpr( "myDateTime" ), ValueExpr.dateTime( "2013-11-29T09:42:00" ) ) );

        then:
        cleanString( expected ) == cleanString( query.toString() )

    }

    def "compare eq geo point"( )
    {
        given:
        def CompareQueryBuilder builder = new CompareQueryBuilder();
        def expected = this.getClass().getResource( "compare_eq_geopoint.json" ).text

        when:
        final QueryBuilder query = builder.build( CompareExpr.eq( new FieldExpr( "myGeoPoint" ), ValueExpr.geoPoint( "59.9127300,10.746090" ) ) );

        then:
        cleanString( expected ) == cleanString( query.toString() )
    }

    def "compare neq string"( )
    {
        given:
        def CompareQueryBuilder builder = new CompareQueryBuilder();
        def expected = this.getClass().getResource( "compare_neq_string.json" ).text

        when:
        final QueryBuilder query = builder.build( CompareExpr.neq( new FieldExpr( "myString" ), ValueExpr.string( "myValue" ) ) );

        then:
        cleanString( expected ) == cleanString( query.toString() )
    }



    def "cleanString"( final String input )
    {
        String output = input.replace( LINE_BREAK, "" );
        output = Strings.trimAllWhitespace( output )
        return output;
    }

}


