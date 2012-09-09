package com.enonic.wem.web.rest.rpc.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;
import com.enonic.wem.web.json.rpc.JsonRpcContext;

import com.enonic.cms.core.timezone.TimeZoneService;

@Component
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

    @Autowired
    public void setTimezoneService( final TimeZoneService timezoneService )
    {
        this.timezoneService = timezoneService;
    }
}
