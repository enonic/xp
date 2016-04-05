package com.enonic.xp.inputtype;

import java.time.Duration;
import java.time.Period;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.ImmutableSet;

public final class RelativeTimeParser
{
    private static final String VALUE_GROUP = "(\\d*)";

    private static final String OPERATOR_GROUP = "([\\+\\-])";

    private static final String UNIT_ENDING = "(?![\\w])";

    public static Result parse( final String timeExpression )
    {
        return getTemporalAmounts( timeExpression, DateTimeUnits.DATE_TIME_UNITS );
    }

    public static Result getTemporalAmounts( final String timeExpression, final Set<String> availableUnits )
    {
        Duration duration = Duration.ZERO;
        Period period = Period.ZERO;
        Boolean isEmpty = true;

        if ( DateTimeUnits.CURRENT_UNITS.contains( timeExpression.trim() ) )
        { //return zero period and duration
            return new Result( duration, period );
        }

        StringBuilder builder = new StringBuilder( OPERATOR_GROUP ).
            append( VALUE_GROUP ).
            append( "\\s*" ).
            append( getPatternGroup( availableUnits ) ).
            append( UNIT_ENDING );

        Matcher m = Pattern.compile( builder.toString(), Pattern.CASE_INSENSITIVE ).matcher( timeExpression );

        while ( m.find() )
        {
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
            isEmpty = false;
        }
        return isEmpty ? null : new Result( duration, period );
    }

    private static String getPatternGroup( Set<String> set )
    {
        return new StringBuilder( "(" ).append( StringUtils.join( set, "|" ) ).append( ")" ).
            toString();
    }

    public static class Result {

        private Duration duration;

        private Period period;

        Result(Duration duration, Period period) {
            this.duration = duration;
            this.period = period;
        }

        public Duration getTime()
        {
            return duration;
        }

        public Period getDate()
        {
            return period;
        }
    }

    public interface DateTimeUnits
    {
        public static final Set<String> YEAR_UNITS = ImmutableSet.of( "year", "years", "y" );

        public static final Set<String> MONTH_UNITS = ImmutableSet.of( "month", "months", "M" );

        public static final Set<String> WEEK_UNITS = ImmutableSet.of( "week", "weeks", "w" );

        public static final Set<String> DAY_UNITS = ImmutableSet.of( "day", "days", "d" );

        public static final Set<String> HOUR_UNITS = ImmutableSet.of( "hour", "hours", "h" );

        public static final Set<String> MINUTE_UNITS = ImmutableSet.of( "minute", "minutes", "m" );

        public static final Set<String> SECOND_UNITS = ImmutableSet.of( "second", "seconds", "s" );

        public static final Set<String> CURRENT_UNITS = ImmutableSet.of( "now", "0" );

        public static final Set<String> PERIOD_UNITS = ImmutableSet.<String>builder().
            addAll( YEAR_UNITS ).
            addAll( MONTH_UNITS ).
            addAll( WEEK_UNITS ).
            addAll( DAY_UNITS ).
            build();

        public static final Set<String> DURATION_UNITS = ImmutableSet.<String>builder().
            addAll( HOUR_UNITS ).
            addAll( MINUTE_UNITS ).
            addAll( SECOND_UNITS ).
            build();

        public static final Set<String> DATE_TIME_UNITS = ImmutableSet.<String>builder().
            addAll( PERIOD_UNITS ).
            addAll( DURATION_UNITS ).
            build();
    }
}
