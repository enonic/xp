package com.enonic.wem.core.search.elastic;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class ElasticsearchFormatter
{
    private ElasticsearchFormatter()
    {
    }

    private static final String XML_DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm";

    private final static SimpleDateFormat ELASTICSEARCH_SIMPLE_DATE_FORMAT = new SimpleDateFormat( XML_DATE_FORMAT_PATTERN );

    private final static SimpleDateFormat ELASTICSEARCH_FULL_DATE_FORMAT = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss:SSS" );

    private final static DateTimeFormatter ELASTICSEARCH_DATE_OPTIONAL_TIME_FORMAT = ISODateTimeFormat.dateOptionalTimeParser();

    public static String formatDateAsStringFull( final Date date )
    {
        return ELASTICSEARCH_FULL_DATE_FORMAT.format( date );
    }

    /*

    public static String formatDateAsStringIgnoreTimezone( final Date date )
    {
        return ELASTICSEARCH_SIMPLE_DATE_FORMAT.format( date );
    }

    public static String formatDateAsStringIgnoreTimezone( final ReadableDateTime date )
    {
        return ELASTICSEARCH_SIMPLE_DATE_FORMAT.format( date.toDateTime().toDate() );
    }

    public static ReadableDateTime toUTCTimeZone( final ReadableDateTime dateTime )
    {
        if ( DateTimeZone.UTC.equals( dateTime.getZone() ) )
        {
            return dateTime;
        }
        final MutableDateTime dateInUTC = dateTime.toMutableDateTime();
        dateInUTC.setZone( DateTimeZone.UTC );
        return dateInUTC.toDateTime();
    }

    public static DateTime parseStringAsElasticsearchDateOptionalTimeFormat( final String dateString )
    {
        try
        {
            return ELASTICSEARCH_DATE_OPTIONAL_TIME_FORMAT.parseDateTime( dateString );
        }
        catch ( IllegalArgumentException e )
        {
            return null;
        }
    }
    */
}
