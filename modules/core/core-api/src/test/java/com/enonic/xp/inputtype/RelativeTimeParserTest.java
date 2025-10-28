package com.enonic.xp.inputtype;

import java.time.Duration;
import java.time.Period;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RelativeTimeParserTest
{

    private final RelativeTimeParser timeParser = new RelativeTimeParser();

    @Test
    void testNow()
    {
        RelativeTime relativeTime = RelativeTimeParser.parse( "now" );

        assertEquals( relativeTime.getTime(), Duration.ZERO );
        assertEquals( relativeTime.getDate(), Period.ZERO );

        relativeTime = RelativeTimeParser.parse( "now+1s" );
        assertNull( relativeTime );
    }

    @Test
    void testZero()
    {
        final RelativeTime relativeTime = RelativeTimeParser.parse( "0" );
        assertEquals( relativeTime.getTime(), Duration.ZERO );
        assertEquals( relativeTime.getDate(), Period.ZERO );
    }

    @Test
    void testSeconds()
    {
        RelativeTime relativeTime = RelativeTimeParser.parse( "+3s " );

        assertEquals( relativeTime.getDate(), Period.ZERO );
        assertEquals( relativeTime.getTime(), Duration.ofSeconds( 3 ) );

        relativeTime = RelativeTimeParser.parse( " -3s" );
        assertEquals( relativeTime.getTime(), Duration.ofSeconds( -3 ) );

        relativeTime = RelativeTimeParser.parse( " +1second " );
        assertEquals( relativeTime.getTime(), Duration.ofSeconds( 1 ) );

        relativeTime = RelativeTimeParser.parse( "  -1second" );
        assertEquals( relativeTime.getTime(), Duration.ofSeconds( -1 ) );

        relativeTime = RelativeTimeParser.parse( "+10seconds  " );
        assertEquals( relativeTime.getTime(), Duration.ofSeconds( 10 ) );

        relativeTime = RelativeTimeParser.parse( " -10 seconds " );
        assertEquals( relativeTime.getTime(), Duration.ofSeconds( -10 ) );
    }

    @Test
    void testMinutes()
    {
        RelativeTime relativeTime = RelativeTimeParser.parse( "+3m " );

        assertEquals( relativeTime.getDate(), Period.ZERO );
        assertEquals( relativeTime.getTime(), Duration.ofMinutes( 3 ) );

        relativeTime = RelativeTimeParser.parse( " -3m" );
        assertEquals( relativeTime.getTime(), Duration.ofMinutes( -3 ) );

        relativeTime = RelativeTimeParser.parse( " +1minute " );
        assertEquals( relativeTime.getTime(), Duration.ofMinutes( 1 ) );

        relativeTime = RelativeTimeParser.parse( "  -1minute" );
        assertEquals( relativeTime.getTime(), Duration.ofMinutes( -1 ) );

        relativeTime = RelativeTimeParser.parse( "+10minutes  " );
        assertEquals( relativeTime.getTime(), Duration.ofMinutes( 10 ) );

        relativeTime = RelativeTimeParser.parse( "-10 minutes" );
        assertEquals( relativeTime.getTime(), Duration.ofMinutes( -10 ) );
    }

    @Test
    void testHours()
    {
        RelativeTime relativeTime = RelativeTimeParser.parse( "+3h " );

        assertEquals( relativeTime.getDate(), Period.ZERO );
        assertEquals( relativeTime.getTime(), Duration.ofHours( 3 ) );

        relativeTime = RelativeTimeParser.parse( " -3h" );
        assertEquals( relativeTime.getTime(), Duration.ofHours( -3 ) );

        relativeTime = RelativeTimeParser.parse( " +1hour " );
        assertEquals( relativeTime.getTime(), Duration.ofHours( 1 ) );

        relativeTime = RelativeTimeParser.parse( "  -1hour" );
        assertEquals( relativeTime.getTime(), Duration.ofHours( -1 ) );

        relativeTime = RelativeTimeParser.parse( "+10hours  " );
        assertEquals( relativeTime.getTime(), Duration.ofHours( 10 ) );

        relativeTime = RelativeTimeParser.parse( " -10 hours " );
        assertEquals( relativeTime.getTime(), Duration.ofHours( -10 ) );
    }

    @Test
    void testTime()
    {
        RelativeTime relativeTime = RelativeTimeParser.parse( "+3h +2m" );

        assertEquals( relativeTime.getDate(), Period.ZERO );
        assertEquals( relativeTime.getTime(), Duration.parse( "PT3h2m" ) );

        relativeTime = RelativeTimeParser.parse( "-3h+2minutes" );
        assertEquals( relativeTime.getTime(), Duration.parse( "PT-3h2m" ) );

        relativeTime = RelativeTimeParser.parse( "+2minutes    -3hours  " );
        assertEquals( relativeTime.getTime(), Duration.parse( "PT-3h2m" ) );

        relativeTime = RelativeTimeParser.parse( "+1minutes-3hours +1s" );
        assertEquals( relativeTime.getTime(), Duration.parse( "-PT2h58m59s" ) );
    }

    @Test
    void testDays()
    {
        RelativeTime relativeTime = RelativeTimeParser.parse( "+3d " );

        assertEquals( relativeTime.getDate(), Period.ofDays( 3 ) );
        assertEquals( relativeTime.getTime(), Duration.ZERO );

        relativeTime = RelativeTimeParser.parse( " -3d" );
        assertEquals( relativeTime.getDate(), Period.ofDays( -3 ) );

        relativeTime = RelativeTimeParser.parse( " +1day " );
        assertEquals( relativeTime.getDate(), Period.ofDays( 1 ) );

        relativeTime = RelativeTimeParser.parse( "  -1day" );
        assertEquals( relativeTime.getDate(), Period.ofDays( -1 ) );

        relativeTime = RelativeTimeParser.parse( "+10days  " );
        assertEquals( relativeTime.getDate(), Period.ofDays( 10 ) );

        relativeTime = RelativeTimeParser.parse( " -10 days " );
        assertEquals( relativeTime.getDate(), Period.ofDays( -10 ) );
    }

    @Test
    void testMonths()
    {
        RelativeTime relativeTime = RelativeTimeParser.parse( "+3M " );

        assertEquals( relativeTime.getDate(), Period.ofMonths( 3 ) );
        assertEquals( relativeTime.getTime(), Duration.ZERO );

        relativeTime = RelativeTimeParser.parse( " -3M" );
        assertEquals( relativeTime.getDate(), Period.ofMonths( -3 ) );

        relativeTime = RelativeTimeParser.parse( " +1month " );
        assertEquals( relativeTime.getDate(), Period.ofMonths( 1 ) );

        relativeTime = RelativeTimeParser.parse( "  -1month" );
        assertEquals( relativeTime.getDate(), Period.ofMonths( -1 ) );

        relativeTime = RelativeTimeParser.parse( "+10months  " );
        assertEquals( relativeTime.getDate(), Period.ofMonths( 10 ) );

        relativeTime = RelativeTimeParser.parse( " -10 months " );
        assertEquals( relativeTime.getDate(), Period.ofMonths( -10 ) );
    }

    @Test
    void testYears()
    {
        RelativeTime relativeTime = RelativeTimeParser.parse( "+3y " );

        assertEquals( relativeTime.getDate(), Period.ofYears( 3 ) );
        assertEquals( relativeTime.getTime(), Duration.ZERO );

        relativeTime = RelativeTimeParser.parse( " -3y" );
        assertEquals( relativeTime.getDate(), Period.ofYears( -3 ) );

        relativeTime = RelativeTimeParser.parse( " +1year " );
        assertEquals( relativeTime.getDate(), Period.ofYears( 1 ) );

        relativeTime = RelativeTimeParser.parse( "  -1year" );
        assertEquals( relativeTime.getDate(), Period.ofYears( -1 ) );

        relativeTime = RelativeTimeParser.parse( "+10years  " );
        assertEquals( relativeTime.getDate(), Period.ofYears( 10 ) );

        relativeTime = RelativeTimeParser.parse( " -10 years " );
        assertEquals( relativeTime.getDate(), Period.ofYears( -10 ) );
    }

    @Test
    void testDate()
    {
        RelativeTime relativeTime = RelativeTimeParser.parse( " +3y +2d " );

        assertEquals( relativeTime.getTime(), Duration.ZERO );
        assertEquals( relativeTime.getDate(), Period.of( 3, 0, 2 ) );

        relativeTime = RelativeTimeParser.parse( "-2years+1month   -24d" );
        assertEquals( relativeTime.getDate(), Period.of( -2, 1, -24 ) );

        relativeTime = RelativeTimeParser.parse( "-3M+2d+1y" );
        assertEquals( relativeTime.getDate(), Period.of( 1, -3, 2 ) );
    }

    @Test
    void testDateTime()
    {
        RelativeTime relativeTime = RelativeTimeParser.parse( " +3M -3m " );

        assertEquals( relativeTime.getTime(), Duration.ofMinutes( -3 ) );
        assertEquals( relativeTime.getDate(), Period.ofMonths( 3 ) );

        relativeTime = RelativeTimeParser.parse( "+2m +3days-1s-2M +23year " );

        assertEquals( relativeTime.getTime(), Duration.parse( "PT2m-1s" ) );
        assertEquals( relativeTime.getDate(), Period.of( 23, -2, 3 ) );

        relativeTime = RelativeTimeParser.parse( "+2years+1month-12d +3h-1minute+24seconds " );

        assertEquals( relativeTime.getTime(), Duration.parse( "PT3h-1m+24s" ) );
        assertEquals( relativeTime.getDate(), Period.of( 2, 1, -12 ) );
    }

    @Test
    void testOverload()
    {
        RelativeTime relativeTime = RelativeTimeParser.parse( " +1s-1s " );

        assertEquals( relativeTime.getTime(), Duration.ofMinutes( 0 ) );
        assertEquals( relativeTime.getDate(), Period.ofMonths( 0 ) );

        relativeTime = RelativeTimeParser.parse( " +1m-60s " );

        assertEquals( relativeTime.getTime(), Duration.ofMinutes( 0 ) );

        relativeTime = RelativeTimeParser.parse( " +61m " );

        assertEquals( relativeTime.getTime(), Duration.parse( "PT1h1m" ) );
    }

    @Test
    void testInvalid()
    {
        RelativeTime relativeTime = RelativeTimeParser.parse( " +1v " );
        assertNull( relativeTime );

        relativeTime = RelativeTimeParser.parse( " +1haur-1sm+1doy " );
        assertNull( relativeTime );

        relativeTime = RelativeTimeParser.parse( " +1haur-1sm+1doy +4M-3s" );
        assertNull( relativeTime );


    }
}
