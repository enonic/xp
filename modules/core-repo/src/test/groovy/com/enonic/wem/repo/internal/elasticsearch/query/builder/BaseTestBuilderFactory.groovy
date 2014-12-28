package com.enonic.wem.repo.internal.elasticsearch.query.builder

import org.elasticsearch.common.xcontent.ToXContent
import org.elasticsearch.common.xcontent.XContentBuilder
import org.elasticsearch.common.xcontent.XContentFactory
import org.elasticsearch.search.facet.FacetBuilder
import spock.lang.Specification

class BaseTestBuilderFactory
    extends Specification
{
    private static final String LINE_BREAK = System.getProperty( "line.separator" )

    private static TimeZone origDefault = TimeZone.getDefault();

    def setupSpec()
    {
        TimeZone.setDefault( TimeZone.getTimeZone( "UTC" ) );
    }

    def cleanupSpec()
    {
        TimeZone.setDefault( origDefault );
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
        output = output.replaceAll( "\\s+", "" );
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
