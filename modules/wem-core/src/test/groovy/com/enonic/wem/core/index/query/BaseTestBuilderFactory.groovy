package com.enonic.wem.core.index.query

import org.elasticsearch.common.Strings
import org.elasticsearch.common.xcontent.ToXContent
import org.elasticsearch.common.xcontent.XContentBuilder
import org.elasticsearch.common.xcontent.XContentFactory
import org.elasticsearch.search.facet.FacetBuilder
import org.joda.time.DateTimeZone
import spock.lang.Specification

class BaseTestBuilderFactory
        extends Specification
{
    private static final String LINE_BREAK = System.getProperty( "line.separator" )

    private static DateTimeZone origDefault = DateTimeZone.getDefault();

    def setupSpec()
    {
        DateTimeZone.setDefault( DateTimeZone.UTC );
    }

    def cleanupSpec()
    {
        DateTimeZone.setDefault( origDefault );
    }

    def "dummy"()
    {
        given:

        when:
        String test = "1"

        then:
        test == "1"
    }

    def cleanString( final String input )
    {
        String output = input.replace( LINE_BREAK, "" );
        output = Strings.trimAllWhitespace( output )
        return output;
    }

    public String getJson( FacetBuilder facetBuilder )
            throws Exception
    {
        final XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        facetBuilder.toXContent( builder, ToXContent.EMPTY_PARAMS );
        builder.endObject();

        return builder.string();
    }

}
