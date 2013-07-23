package com.enonic.wem.admin.rest.resource.util;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.joda.time.DateTimeZone;

import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.util.model.TimeZoneListJson;
import com.enonic.wem.core.time.TimeZoneService;

@Path("util/timezone")
@Produces(MediaType.APPLICATION_JSON)
public class TimeZoneResource
    extends AbstractResource
{
    private TimeZoneService timezoneService;

    @GET
    public TimeZoneListJson list()
    {
        final List<DateTimeZone> timeZones = this.timezoneService.getTimeZones();
        final TimeZoneListJson result = new TimeZoneListJson( timeZones );

        return result;
    }

    @Inject
    public void setTimezoneService( final TimeZoneService timezoneService )
    {
        this.timezoneService = timezoneService;
    }
}