package com.enonic.xp.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class DateTimeHelperTest
{

    @Test
    void supported_iso_datetime_formats()
    {
        assertNotNull( DateTimeHelper.parseIsoDateTime( "2016-10-05T10:00:00Z" ) );
        assertNotNull( DateTimeHelper.parseIsoDateTime( "2016-10-05T10:00:00+01:00" ) );
        assertNotNull( DateTimeHelper.parseIsoDateTime( "2016-10-05T10:00:00+01:30" ) );
        assertNotNull( DateTimeHelper.parseIsoDateTime( "2016-10-05T10:00:00.000+01:30" ) );
    }

    @Test
    void empty_iso_datetime_formats()
    {
        assertNull( DateTimeHelper.parseIsoDateTime( "" ) );
        assertNull( DateTimeHelper.parseIsoDateTime( null ) );
    }
}
