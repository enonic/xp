package com.enonic.wem.web.rest2.resource.timezone;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.timezone.TimeZoneService;

@Path("misc/timezone")
@Produces(MediaType.APPLICATION_JSON)
@Component
public final class TimeZoneResource
{
    private TimeZoneService timezoneService;

    @GET
    public TimeZoneResult getAll()
    {
        return new TimeZoneResult( this.timezoneService.getTimeZones() );
    }

    @Autowired
    public void setTimezoneService( final TimeZoneService timezoneService )
    {
        this.timezoneService = timezoneService;
    }
}
