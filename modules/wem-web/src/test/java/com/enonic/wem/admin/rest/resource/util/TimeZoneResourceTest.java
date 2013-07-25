package com.enonic.wem.admin.rest.resource.util;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.TestUtil;
import com.enonic.wem.admin.rest.resource.util.model.TimeZoneJson;
import com.enonic.wem.admin.rest.resource.util.model.TimeZoneListJson;
import com.enonic.wem.api.Client;
import com.enonic.wem.core.time.TimeZoneService;

import static org.junit.Assert.*;

public class TimeZoneResourceTest
{

    private Client client;
    private TimeZoneService timezoneService;

    @Before
    public void setup()
    {
        client = Mockito.mock( Client.class );
        timezoneService = Mockito.mock( TimeZoneService.class );

        final List<DateTimeZone> zones = new ArrayList<>( 3 );
        zones.add( DateTimeZone.UTC );
        zones.add( DateTimeZone.forID( "EST" ) );
        zones.add( DateTimeZone.forID( "Asia/Tokyo" ) );
        zones.add( DateTimeZone.forID( "America/Caracas" ) );

        Mockito.when( timezoneService.getTimeZones() ).thenReturn( zones );
    }

    @Test
    public void testList()
        throws Exception
    {
        final TimeZoneResource resource = new TimeZoneResource();
        resource.setClient( client );
        resource.setTimezoneService( timezoneService );

        TimeZoneListJson result = resource.list();

        Mockito.verify( timezoneService, Mockito.times( 1 ) ).getTimeZones();

        assertNotNull( result );
        assertEquals( 4, result.getTotal() );

        List<String> names = new ArrayList<>( 4 );
        for ( final TimeZoneJson model : result.getTimezones() )
        {
            names.add( model.getId() );
        }

        TestUtil.assertUnorderedArraysEquals( new String[]{"UTC", "EST", "Asia/Tokyo", "America/Caracas"}, names.toArray() );
    }
}
