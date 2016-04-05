package com.enonic.xp.impl.macro;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import com.enonic.xp.macro.MacroContext;
import com.enonic.xp.macro.MacroProcessor;

public class ModifiedDateMacroProcessor
    implements MacroProcessor
{
    private final static DateTimeFormatter DEFAULT_DATE_FORMAT = DateTimeFormatter.
        ofPattern( "yyyy-MM-dd HH:mm:ss" ).
        withZone( ZoneOffset.UTC );

    private final static String MODIFIED_DATE_PARAM = "modified_date";

    private final static String MODIFIED_DATE_FOPMAT_PARAM = "modified_date_format";

    @Override
    public String process( final MacroContext context )
    {
        final String modifiedDate = context.getParam( MODIFIED_DATE_PARAM );
        if ( modifiedDate == null )
        {
            return null;
        }

        final String modifiedDateFormat = context.getParam( MODIFIED_DATE_FOPMAT_PARAM );

        try
        {
            final Instant instant = Instant.parse( modifiedDate );
            if ( modifiedDateFormat != null )
            {
                return DateTimeFormatter.ofPattern( modifiedDateFormat ).withZone( ZoneOffset.UTC ).format( instant );
            }

            return DEFAULT_DATE_FORMAT.format( instant );
        }
        catch ( final Exception e )
        {
            return null;
        }
    }
}
