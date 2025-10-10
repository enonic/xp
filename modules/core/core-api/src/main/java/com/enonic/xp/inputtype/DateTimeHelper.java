package com.enonic.xp.inputtype;

import java.time.Instant;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;

final class DateTimeHelper
{
    private DateTimeHelper()
    {
        throw new AssertionError();
    }

    static ZonedDateTime resolveRelativeTime( final String defaultValue )
    {
        final RelativeTime result = RelativeTimeParser.parse( defaultValue );
        if ( result == null )
        {
            throw new IllegalArgumentException( String.format( "Invalid DateTime format: %s", defaultValue ) );
        }

        final Instant instant = Instant.now().plus( result.getTime() );
        final Period period = result.getDate();

        return instant.atZone( ZoneId.systemDefault() )
            .plusYears( period.getYears() )
            .plusMonths( period.getMonths() )
            .plusDays( period.getDays() )
            .withNano( 0 );
    }
}
