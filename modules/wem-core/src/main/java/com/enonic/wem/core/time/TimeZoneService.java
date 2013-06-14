package com.enonic.wem.core.time;

import java.util.List;

import org.joda.time.DateTimeZone;

import com.google.inject.ImplementedBy;

@ImplementedBy(TimeZoneServiceImpl.class)
public interface TimeZoneService
{
    List<DateTimeZone> getTimeZones();
}
