package com.enonic.xp.util;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import com.google.common.base.Strings;

public class DateTimeHelper
{
    private static final DateTimeFormatter ISO_DATETIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public static Instant parseIsoDateTime( final String value )
    {
        if ( Strings.isNullOrEmpty( value ) )
        {
            return null;
        }

        final TemporalAccessor ta = ISO_DATETIME_FORMATTER.parse( value );

        return Instant.from( ta );
    }

}
