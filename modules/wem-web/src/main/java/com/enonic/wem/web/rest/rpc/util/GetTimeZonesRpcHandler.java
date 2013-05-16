package com.enonic.wem.web.rest.rpc.util;

import javax.inject.Inject;

import com.enonic.wem.core.time.TimeZoneService;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;


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
