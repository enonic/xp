package com.enonic.wem.admin.rest.rpc.util;

import java.util.List;

import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.wem.admin.jsonrpc.JsonRpcHandler;
import com.enonic.wem.admin.rest.rpc.AbstractRpcHandlerTest;
import com.enonic.wem.core.time.TimeZoneService;

public class GetTimeZonesRpcHandlerTest
    extends AbstractRpcHandlerTest
{
    private List<DateTimeZone> timeZoneList;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        this.timeZoneList = Lists.newArrayList();

        final TimeZoneService service = Mockito.mock( TimeZoneService.class );
        Mockito.when( service.getTimeZones() ).thenReturn( this.timeZoneList );

        final GetTimeZonesRpcHandler handler = new GetTimeZonesRpcHandler();
        handler.setTimezoneService( service );
        return handler;
    }

    @Test
    public void testRequest()
        throws Exception
    {
        this.timeZoneList.add( DateTimeZone.UTC );
        this.timeZoneList.add( DateTimeZone.forID( "EST" ) );
        this.timeZoneList.add( DateTimeZone.forID( "Asia/Tokyo" ) );
        this.timeZoneList.add( DateTimeZone.forID( "America/Caracas" ) );

        testSuccess( "getTimeZones_result.json" );
    }
}
