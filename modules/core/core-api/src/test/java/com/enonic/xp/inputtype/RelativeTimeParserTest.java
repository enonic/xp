package com.enonic.xp.inputtype;

import java.time.Duration;
import java.time.Period;

import org.junit.Test;

import static com.enonic.xp.inputtype.RelativeTimeParser.Result;
import static org.junit.Assert.*;

public class RelativeTimeParserTest
{

    private final RelativeTimeParser timeParser = new RelativeTimeParser();

    @Test
    public void testNow()
    {
        Result result = timeParser.parse( "now" );

        assertEquals( result.getTime(), Duration.ZERO );
        assertEquals( result.getDate(), Period.ZERO );

        result = timeParser.parse( "now+1s" );
        assertEquals( result.getTime(), Duration.parse( "PT1s" ) );

        result = timeParser.parse( "-1y now" );
        assertEquals( result.getDate(), Period.ofYears( -1 ) );
    }

    @Test
    public void testZero()
    {
        final Result result = timeParser.parse( "0" );
        assertEquals( result.getTime(), Duration.ZERO );
        assertEquals( result.getDate(), Period.ZERO );
    }

    @Test
    public void testSeconds()
    {
        Result result = timeParser.parse( "+3s " );

        assertEquals( result.getDate(), Period.ZERO );
        assertEquals( result.getTime(), Duration.ofSeconds( 3 ) );

        result = timeParser.parse( " -3s" );
        assertEquals( result.getTime(), Duration.ofSeconds( -3 ) );

        result = timeParser.parse( " +1second " );
        assertEquals( result.getTime(), Duration.ofSeconds( 1 ) );

        result = timeParser.parse( "  -1second" );
        assertEquals( result.getTime(), Duration.ofSeconds( -1 ) );

        result = timeParser.parse( "+10seconds  " );
        assertEquals( result.getTime(), Duration.ofSeconds( 10 ) );

        result = timeParser.parse( " -10 seconds " );
        assertEquals( result.getTime(), Duration.ofSeconds( -10 ) );
    }

    @Test
    public void testMinutes()
    {
        Result result = timeParser.parse( "+3m " );

        assertEquals( result.getDate(), Period.ZERO );
        assertEquals( result.getTime(), Duration.ofMinutes( 3 ) );

        result = timeParser.parse( " -3m" );
        assertEquals( result.getTime(), Duration.ofMinutes( -3 ) );

        result = timeParser.parse( " +1minute " );
        assertEquals( result.getTime(), Duration.ofMinutes( 1 ) );

        result = timeParser.parse( "  -1minute" );
        assertEquals( result.getTime(), Duration.ofMinutes( -1 ) );

        result = timeParser.parse( "+10minutes  " );
        assertEquals( result.getTime(), Duration.ofMinutes( 10 ) );

        result = timeParser.parse( "-10 minutes" );
        assertEquals( result.getTime(), Duration.ofMinutes( -10 ) );
    }

    @Test
    public void testHours()
    {
        Result result = timeParser.parse( "+3h " );

        assertEquals( result.getDate(), Period.ZERO );
        assertEquals( result.getTime(), Duration.ofHours( 3 ) );

        result = timeParser.parse( " -3h" );
        assertEquals( result.getTime(), Duration.ofHours( -3 ) );

        result = timeParser.parse( " +1hour " );
        assertEquals( result.getTime(), Duration.ofHours( 1 ) );

        result = timeParser.parse( "  -1hour" );
        assertEquals( result.getTime(), Duration.ofHours( -1 ) );

        result = timeParser.parse( "+10hours  " );
        assertEquals( result.getTime(), Duration.ofHours( 10 ) );

        result = timeParser.parse( " -10 hours " );
        assertEquals( result.getTime(), Duration.ofHours( -10 ) );
    }

    @Test
    public void testTime()
    {
        Result result = timeParser.parse( "+3h +2m" );

        assertEquals( result.getDate(), Period.ZERO );
        assertEquals( result.getTime(), Duration.parse( "PT3h2m" ) );

        result = timeParser.parse( "-3h+2minutes" );
        assertEquals( result.getTime(), Duration.parse( "PT-3h2m" ) );

        result = timeParser.parse( "+2minutes    -3hours  " );
        assertEquals( result.getTime(), Duration.parse( "PT-3h2m" ) );

        result = timeParser.parse( "+1minutes-3hours +1s" );
        assertEquals( result.getTime(), Duration.parse( "-PT2h58m59s" ) );
    }

    @Test
    public void testDays()
    {
        Result result = timeParser.parse( "+3d " );

        assertEquals( result.getDate(), Period.ofDays( 3 ) );
        assertEquals( result.getTime(), Duration.ZERO );

        result = timeParser.parse( " -3d" );
        assertEquals( result.getDate(), Period.ofDays( -3 ) );

        result = timeParser.parse( " +1day " );
        assertEquals( result.getDate(), Period.ofDays( 1 ) );

        result = timeParser.parse( "  -1day" );
        assertEquals( result.getDate(), Period.ofDays( -1 ) );

        result = timeParser.parse( "+10days  " );
        assertEquals( result.getDate(), Period.ofDays( 10 ) );

        result = timeParser.parse( " -10 days " );
        assertEquals( result.getDate(), Period.ofDays( -10 ) );
    }

    @Test
    public void testMonths()
    {
        Result result = timeParser.parse( "+3M " );

        assertEquals( result.getDate(), Period.ofMonths( 3 ) );
        assertEquals( result.getTime(), Duration.ZERO );

        result = timeParser.parse( " -3M" );
        assertEquals( result.getDate(), Period.ofMonths( -3 ) );

        result = timeParser.parse( " +1month " );
        assertEquals( result.getDate(), Period.ofMonths( 1 ) );

        result = timeParser.parse( "  -1month" );
        assertEquals( result.getDate(), Period.ofMonths( -1 ) );

        result = timeParser.parse( "+10months  " );
        assertEquals( result.getDate(), Period.ofMonths( 10 ) );

        result = timeParser.parse( " -10 months " );
        assertEquals( result.getDate(), Period.ofMonths( -10 ) );
    }

    @Test
    public void testYears()
    {
        Result result = timeParser.parse( "+3y " );

        assertEquals( result.getDate(), Period.ofYears( 3 ) );
        assertEquals( result.getTime(), Duration.ZERO );

        result = timeParser.parse( " -3y" );
        assertEquals( result.getDate(), Period.ofYears( -3 ) );

        result = timeParser.parse( " +1year " );
        assertEquals( result.getDate(), Period.ofYears( 1 ) );

        result = timeParser.parse( "  -1year" );
        assertEquals( result.getDate(), Period.ofYears( -1 ) );

        result = timeParser.parse( "+10years  " );
        assertEquals( result.getDate(), Period.ofYears( 10 ) );

        result = timeParser.parse( " -10 years " );
        assertEquals( result.getDate(), Period.ofYears( -10 ) );
    }

    @Test
    public void testDate()
    {
        Result result = timeParser.parse( " +3y +2d " );

        assertEquals( result.getTime(), Duration.ZERO );
        assertEquals( result.getDate(), Period.of( 3, 0, 2 ) );

        result = timeParser.parse( "-2years+1month   -24d" );
        assertEquals( result.getDate(), Period.of( -2, 1, -24 ) );

        result = timeParser.parse( "-3M+2d+1y" );
        assertEquals( result.getDate(), Period.of( 1, -3, 2 ) );
    }

    @Test
    public void testDateTime()
    {
        Result result = timeParser.parse( " +3M -3m " );

        assertEquals( result.getTime(), Duration.ofMinutes( -3 ) );
        assertEquals( result.getDate(), Period.ofMonths( 3 ) );

        result = timeParser.parse( "+2m +3days-1s-2M +23year " );

        assertEquals( result.getTime(), Duration.parse( "PT2m-1s" ) );
        assertEquals( result.getDate(), Period.of( 23, -2, 3 ) );

        result = timeParser.parse( "+2years+1month-12d +3h-1minute+24seconds " );

        assertEquals( result.getTime(), Duration.parse( "PT3h-1m+24s" ) );
        assertEquals( result.getDate(), Period.of( 2, 1, -12 ) );
    }

    @Test
    public void testOverload()
    {
        Result result = timeParser.parse( " +1s-1s " );

        assertEquals( result.getTime(), Duration.ofMinutes( 0 ) );
        assertEquals( result.getDate(), Period.ofMonths( 0 ) );

        result = timeParser.parse( " +1m-60s " );

        assertEquals( result.getTime(), Duration.ofMinutes( 0 ) );

        result = timeParser.parse( " +61m " );

        assertEquals( result.getTime(), Duration.parse( "PT1h1m" ) );
    }

    @Test
    public void testInvalid()
    {
        Result result = timeParser.parse( " +1v " );
        assertNull( result );

        result = timeParser.parse( " +1haur-1sm+1doy " );
        assertNull( result );

        result = timeParser.parse( " +1haur-1sm+1doy +4M-3s" );

        assertEquals( result.getDate(), Period.ofMonths( 4 ) );
        assertEquals( result.getTime(), Duration.ofSeconds( -3 ) );


    }
}
