package com.enonic.wem.repo.internal.elasticsearch;


import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

class IndexFormats
{
    public final static DateTimeFormatter FULL_DATE_FORMAT =
        java.time.format.DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss.SSS" ).withZone( ZoneOffset.UTC );
}
