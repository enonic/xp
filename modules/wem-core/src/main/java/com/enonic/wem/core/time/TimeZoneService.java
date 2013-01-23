package com.enonic.wem.core.time;

import java.util.List;

import org.joda.time.DateTimeZone;

public interface TimeZoneService
{
    List<DateTimeZone> getTimeZones();
}
