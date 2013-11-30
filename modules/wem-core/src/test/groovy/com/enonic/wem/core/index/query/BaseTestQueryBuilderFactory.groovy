package com.enonic.wem.core.index.query

import org.elasticsearch.common.Strings
import org.joda.time.DateTimeZone
import spock.lang.Specification

class BaseTestQueryBuilderFactory extends Specification
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


    def "cleanString"( final String input )
    {
        String output = input.replace( LINE_BREAK, "" );
        output = Strings.trimAllWhitespace( output )
        return output;
    }

}
