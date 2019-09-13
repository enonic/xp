package com.enonic.xp.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DateTimeHelperTest
{

    @Test
    public void supported_iso_datetime_formats()
        throws Exception
    {
        assertNotNull( DateTimeHelper.parseIsoDateTime( "2016-10-05T10:00:00Z" ) );
        assertNotNull( DateTimeHelper.parseIsoDateTime( "2016-10-05T10:00:00+01:00" ) );
        assertNotNull( DateTimeHelper.parseIsoDateTime( "2016-10-05T10:00:00+01:30" ) );
        assertNotNull( DateTimeHelper.parseIsoDateTime( "2016-10-05T10:00:00.000+01:30" ) );
    }

    @Test
    public void empty_iso_datetime_formats()
        throws Exception
    {
        assertNull( DateTimeHelper.parseIsoDateTime( "" ) );
        assertNull( DateTimeHelper.parseIsoDateTime( null ) );
    }
}
