package com.enonic.wem.portal.request

import spock.lang.Specification
import spock.lang.Unroll

class RequestProtocolTest extends Specification
{
    @Unroll
    def "test protocol #protocol"( )
    {
        expect:
        protocol.scheme == scheme
        protocol.defaultPort == port

        where:
        protocol              | scheme  | port
        RequestProtocol.HTTP  | 'http'  | 80
        RequestProtocol.HTTPS | 'https' | 443
    }
}
