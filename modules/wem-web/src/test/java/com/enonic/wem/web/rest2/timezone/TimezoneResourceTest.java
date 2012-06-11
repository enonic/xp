package com.enonic.wem.web.rest2.timezone;

import java.util.List;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

import com.google.common.collect.Lists;

import com.enonic.wem.web.rest2.AbstractResourceTest;
import com.enonic.wem.web.rest2.provider.ObjectMapperFactory;

import com.enonic.cms.core.timezone.TimeZoneService;

public class TimeZoneResourceTest
    extends AbstractResourceTest
{
    private TimeZoneResource resource;

    private List<DateTimeZone> timeZoneList;

    @Before
    public void setUp()
    {
        this.timeZoneList = Lists.newArrayList();

        final TimeZoneService service = Mockito.mock( TimeZoneService.class );
        Mockito.when( service.getTimeZones() ).thenReturn( this.timeZoneList );

        this.resource = new TimeZoneResource();
        this.resource.setTimezoneService( service );
    }

    @Test
    public void testGetAll_empty()
        throws Exception
    {
        this.timeZoneList.clear();
        final TimeZoneResult result = this.resource.getAll();
        assertJsonResult( "TimeZoneResourceTest_getAll_empty.json", result );
    }

    @Test
    public void testGetAll_list()
        throws Exception
    {
        this.timeZoneList.add( DateTimeZone.UTC );
        this.timeZoneList.add( DateTimeZone.forID( "EST" ) );

        final TimeZoneResult result = this.resource.getAll();
        assertJsonResult( "TimeZoneResourceTest_getAll_list.json", result );
    }
}
