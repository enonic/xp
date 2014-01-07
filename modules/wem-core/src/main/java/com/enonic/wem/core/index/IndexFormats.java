package com.enonic.wem.core.index;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class IndexFormats
{

    public final static DateTimeFormatter FULL_DATE_FORMAT = DateTimeFormat.forPattern( "yyyy-MM-dd HH:mm:ss.SSS" );
}
