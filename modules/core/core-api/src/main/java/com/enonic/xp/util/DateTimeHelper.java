package com.enonic.xp.util;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import static com.google.common.base.Strings.isNullOrEmpty;

public final class DateTimeHelper
{
    private static final DateTimeFormatter ISO_DATETIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public static Instant parseIsoDateTime( final String value )
    {
        if ( isNullOrEmpty( value ) )
        {
            return null;
        }

        final TemporalAccessor ta = ISO_DATETIME_FORMATTER.parse( value );

        return Instant.from( ta );
    }

    private DateTimeHelper()
    {
    }
}
