package com.enonic.wem.admin.rpc.util;

import javax.inject.Inject;

import com.enonic.wem.admin.jsonrpc.JsonRpcContext;
import com.enonic.wem.admin.rpc.AbstractDataRpcHandler;
import com.enonic.wem.core.time.TimeZoneService;


public final class GetTimeZonesRpcHandler
    extends AbstractDataRpcHandler
{
    private TimeZoneService timezoneService;

    public GetTimeZonesRpcHandler()
    {
        super( "util_getTimeZones" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final TimeZoneJsonResult result = new TimeZoneJsonResult( this.timezoneService.getTimeZones() );
        context.setResult( result );
    }

    @Inject
    public void setTimezoneService( final TimeZoneService timezoneService )
    {
        this.timezoneService = timezoneService;
    }
}
