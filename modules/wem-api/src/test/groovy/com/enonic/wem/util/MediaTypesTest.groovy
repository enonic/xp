package com.enonic.wem.util

import com.google.common.net.MediaType
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class MediaTypesTest extends Specification
{
    def "resolve file extension #ext to #type"( )
    {
        given:
        def mediaTypes = MediaTypes.instance()
        mediaTypes.clear();
        mediaTypes.put( "html", MediaType.HTML_UTF_8 );

        expect:
        mediaTypes.fromExt( ext ) != null
        type == mediaTypes.fromExt( ext ).toString()

        where:
        ext    | type
        "html" | "text/html"
        "any"  | "application/octet-stream"
    }

    def "resolve file name #name to #type"( )
    {
        given:
        def mediaTypes = MediaTypes.instance()
        mediaTypes.clear();
        mediaTypes.put( "html", MediaType.HTML_UTF_8 );

        expect:
        mediaTypes.fromFile( name ) != null
        type == mediaTypes.fromFile( name ).toString()

        where:
        name         | type
        "index.html" | "text/html"
        "file"       | "application/octet-stream"
    }
}
