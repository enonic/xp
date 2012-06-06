package com.enonic.wem.web.rest.timezone;

import java.util.List;

import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enonic.cms.core.timezone.TimeZoneService;

@Controller
@RequestMapping( value = "/misc/timezone", produces = MediaType.APPLICATION_JSON_VALUE)
public final class TimezoneController
{

    @Autowired
    private TimeZoneService timezoneService;

    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ResponseBody
    public TimezonesModel getAll()
    {
        final List<DateTimeZone> list = this.timezoneService.getTimeZones();
        return TimezoneModelTranslator.toModel(list);
    }

}
