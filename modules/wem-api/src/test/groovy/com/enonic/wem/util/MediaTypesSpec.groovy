package com.enonic.wem.util

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class MediaTypesSpec extends Specification
{
    def "get type (#type) from extension (#ext)"( )
    {
        given:
        def mediaTypes = MediaTypes.instance()

        expect:
        mediaTypes.fromExt( ext ) != null
        type == mediaTypes.fromExt( ext ).toString()

        where:
        type << ["text/html", "application/octet-stream"]
        ext << ["html", "any"]
    }
}
