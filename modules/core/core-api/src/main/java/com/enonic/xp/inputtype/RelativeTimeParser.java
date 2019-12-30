package com.enonic.xp.inputtype;

import java.time.Duration;
import java.time.Period;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
final class RelativeTimeParser
{
    private static final String VALUE_GROUP = "(\\d*)";

    private static final String OPERATOR_GROUP = "([\\+\\-])";

    private static final String UNIT_ENDING = "(?![\\w])";

    static RelativeTime parse( final String timeExpression )
    {
        return getTemporalAmounts( timeExpression, DateTimeUnits.DATE_TIME_UNITS );
    }

    private static RelativeTime getTemporalAmounts( final String timeExpression, final Set<String> availableUnits )
    {
        Duration duration = Duration.ZERO;
        Period period = Period.ZERO;

        final String trimedExpression = timeExpression.replaceAll( "\\s", "" );

        if ( DateTimeUnits.CURRENT_UNITS.contains( trimedExpression ) )
        { //return zero period and duration
            return new RelativeTime( duration, period );
        }

        StringBuilder builder = new StringBuilder( OPERATOR_GROUP ).
            append( VALUE_GROUP ).
            append( "\\s*" ).
            append( getPatternGroup( availableUnits ) ).
            append( UNIT_ENDING );

        final Matcher m = Pattern.compile( builder.toString(), Pattern.CASE_INSENSITIVE ).matcher( trimedExpression );
        final StringBuilder actualExpression = new StringBuilder();

        while ( m.find() )
        {
            actualExpression.append( m.group( 0 ) );

            final String operatorString = m.group( 1 );
            final String valueString = m.group( 2 );
            final String unitTypeString = m.group( 3 );

            if ( DateTimeUnits.PERIOD_UNITS.contains( unitTypeString ) )
            {
                period = period.plus( Period.parse( "P" + operatorString + valueString + unitTypeString.substring( 0, 1 ) ) );
            }
            else if ( DateTimeUnits.DURATION_UNITS.contains( unitTypeString ) )
            {
                duration = duration.plus( Duration.parse( "PT" + operatorString + valueString + unitTypeString.substring( 0, 1 ) ) );
            }
        }
        return trimedExpression.equals( actualExpression.toString() ) // check for whole expression is valid
            ? new RelativeTime( duration, period ) : null;
    }

    private static String getPatternGroup( Set<String> set )
    {
        return "(" + String.join( "|", set ) + ")";
    }

    private interface DateTimeUnits
    {
        Set<String> YEAR_UNITS = Set.of( "year", "years", "y" );

        Set<String> MONTH_UNITS = Set.of( "month", "months", "M" );

        Set<String> WEEK_UNITS = Set.of( "week", "weeks", "w" );

        Set<String> DAY_UNITS = Set.of( "day", "days", "d" );

        Set<String> HOUR_UNITS = Set.of( "hour", "hours", "h" );

        Set<String> MINUTE_UNITS = Set.of( "minute", "minutes", "m" );

        Set<String> SECOND_UNITS = Set.of( "second", "seconds", "s" );

        Set<String> CURRENT_UNITS = Set.of( "now", "0" );

        Set<String> PERIOD_UNITS = ImmutableSet.<String>builder().
            addAll( YEAR_UNITS ).
            addAll( MONTH_UNITS ).
            addAll( WEEK_UNITS ).
            addAll( DAY_UNITS ).
            build();

        Set<String> DURATION_UNITS = ImmutableSet.<String>builder().
            addAll( HOUR_UNITS ).
            addAll( MINUTE_UNITS ).
            addAll( SECOND_UNITS ).
            build();

        Set<String> DATE_TIME_UNITS = ImmutableSet.<String>builder().
            addAll( PERIOD_UNITS ).
            addAll( DURATION_UNITS ).
            build();
    }
}