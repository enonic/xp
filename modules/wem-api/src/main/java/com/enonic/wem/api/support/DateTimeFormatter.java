package com.enonic.wem.api.support;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

public class DateTimeFormatter
{
    private static final org.joda.time.format.DateTimeFormatter isoDateTimePrinter = ISODateTimeFormat.dateTime().withZoneUTC();

    private static final org.joda.time.format.DateTimeFormatter isoDateTimeParser = ISODateTimeFormat.dateTimeParser();


    public static String format( final DateTime dateTime )
    {
        if ( dateTime == null )
        {
            return null;
        }
        return isoDateTimePrinter.print( dateTime );
    }

    public static DateTime parse( java.lang.String dateTimeString )
    {
        if ( dateTimeString == null || dateTimeString.isEmpty() )
        {
            return null;
        }

        return isoDateTimeParser.parseDateTime( dateTimeString );

    }


}
