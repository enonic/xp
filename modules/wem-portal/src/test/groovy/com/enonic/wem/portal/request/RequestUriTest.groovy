package com.enonic.wem.portal.request

import spock.lang.Specification
import spock.lang.Unroll

class RequestUriTest extends Specification
{
    @Unroll
    def "uri with protocol (#protocol) and port (#port)"( )
    {
        def uri = RequestUri.builder().protocol( protocol ).host( 'myhost' ).port( port ).build();

        expect:
        uri != null
        uri.toString() == expected

        where:
        protocol | port | expected
        'http'   | 80   | 'http://myhost'
        'http'   | 111  | 'http://myhost:111'
        'https'  | 222  | 'https://myhost:222'
        'https'  | 443  | 'https://myhost'
    }
}
