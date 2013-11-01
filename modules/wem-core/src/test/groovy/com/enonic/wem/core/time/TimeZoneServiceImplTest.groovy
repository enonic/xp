package com.enonic.wem.core.time

import spock.lang.Specification

class TimeZoneServiceImplTest extends Specification
{
    def "expect more than zero timezones"( )
    {
        given:
        def service = new TimeZoneServiceImpl()

        when:
        def zones = service.getTimeZones()

        then:
        zones != null
        zones.size() > 0
    }
}
