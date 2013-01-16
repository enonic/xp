package com.enonic.wem.core.time;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

@Component
public final class TimeServiceImpl
    implements TimeService
{
    @Override
    public DateTime getNowAsDateTime()
    {
        return DateTime.now();
    }
}
