package com.enonic.wem.admin.rest.resource.util;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest2;
import com.enonic.wem.core.time.TimeZoneService;

public class TimeZoneResourceTest
    extends AbstractResourceTest2
{
    @Override
    protected Object getResourceInstance()
    {
        final TimeZoneService timezoneService = Mockito.mock( TimeZoneService.class );

        final List<DateTimeZone> zones = new ArrayList<>( 3 );
        zones.add( DateTimeZone.UTC );
        zones.add( DateTimeZone.forID( "EST" ) );
        zones.add( DateTimeZone.forID( "Asia/Tokyo" ) );
        zones.add( DateTimeZone.forID( "America/Caracas" ) );

        Mockito.when( timezoneService.getTimeZones() ).thenReturn( zones );

        final TimeZoneResource resource = new TimeZoneResource();
        resource.setTimezoneService( timezoneService );
        return resource;
    }

    @Test
    public void testList()
        throws Exception
    {
        final String json = resource().path( "util/timezone" ).get( String.class );
        assertJson( "timezone_list.json", json );
    }
}
