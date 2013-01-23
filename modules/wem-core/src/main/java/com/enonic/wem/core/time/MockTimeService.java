package com.enonic.wem.core.time;

import org.joda.time.DateTime;

public final class MockTimeService
    implements TimeService
{
    private DateTime timeNow;

    public MockTimeService( final DateTime timeNow )
    {
        this.timeNow = timeNow;
    }

    @Override
    public DateTime getNowAsDateTime()
    {
        return this.timeNow;
    }
}
