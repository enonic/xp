package com.enonic.xp.repo.impl.elasticsearch;


import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class IndexFormats
{
    public final static DateTimeFormatter FULL_DATE_FORMAT =
        java.time.format.DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss.SSS" ).withZone( ZoneOffset.UTC );
}
