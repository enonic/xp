package com.enonic.xp.inputtype;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

public class RelativeTimeParserTest
{

    private final RelativeTimeParser timeParser = new RelativeTimeParser();

    @Test
    public void testNow()
    {
        List<TemporalAmount> temporalAmounts = timeParser.parse( "now" );

        assertEquals( temporalAmounts.size(), 2 );
        assertTrue( temporalAmounts.contains( Duration.ZERO ) );
        assertTrue( temporalAmounts.contains( Period.ZERO ) );

        temporalAmounts = timeParser.parse( "now+1s" );
        assertTrue( temporalAmounts.contains( Duration.parse( "PT1s" ) ) );

        temporalAmounts = timeParser.parse( "-1y now" );
        assertTrue( temporalAmounts.contains( Period.ofYears( -1 ) ) );
    }

    @Test
    public void testZero()
    {
        final List<TemporalAmount> temporalAmounts = timeParser.parse( "0" );
        assertEquals( temporalAmounts.size(), 2 );
        assertTrue( temporalAmounts.contains( Duration.ZERO ) );
        assertTrue( temporalAmounts.contains( Period.ZERO ) );
    }

    @Test
    public void testSeconds()
    {
        List<TemporalAmount> temporalAmounts = timeParser.parse( "+3s " );

        assertEquals( temporalAmounts.size(), 2 );
        assertTrue( temporalAmounts.contains( Period.ZERO ) );
        assertTrue( temporalAmounts.contains( Duration.ofSeconds( 3 ) ) );

        temporalAmounts = timeParser.parse( " -3s" );
        assertTrue( temporalAmounts.contains( Duration.ofSeconds( -3 ) ) );

        temporalAmounts = timeParser.parse( " +1second " );
        assertTrue( temporalAmounts.contains( Duration.ofSeconds( 1 ) ) );

        temporalAmounts = timeParser.parse( "  -1second" );
        assertTrue( temporalAmounts.contains( Duration.ofSeconds( -1 ) ) );

        temporalAmounts = timeParser.parse( "+10seconds  " );
        assertTrue( temporalAmounts.contains( Duration.ofSeconds( 10 ) ) );

        temporalAmounts = timeParser.parse( " -10 seconds " );
        assertTrue( temporalAmounts.contains( Duration.ofSeconds( -10 ) ) );
    }

    @Test
    public void testMinutes()
    {
        List<TemporalAmount> temporalAmounts = timeParser.parse( "+3m " );

        assertEquals( temporalAmounts.size(), 2 );
        assertTrue( temporalAmounts.contains( Period.ZERO ) );
        assertTrue( temporalAmounts.contains( Duration.ofMinutes( 3 ) ) );

        temporalAmounts = timeParser.parse( " -3m" );
        assertTrue( temporalAmounts.contains( Duration.ofMinutes( -3 ) ) );

        temporalAmounts = timeParser.parse( " +1minute " );
        assertTrue( temporalAmounts.contains( Duration.ofMinutes( 1 ) ) );

        temporalAmounts = timeParser.parse( "  -1minute" );
        assertTrue( temporalAmounts.contains( Duration.ofMinutes( -1 ) ) );

        temporalAmounts = timeParser.parse( "+10minutes  " );
        assertTrue( temporalAmounts.contains( Duration.ofMinutes( 10 ) ) );

        temporalAmounts = timeParser.parse( "-10 minutes" );
        assertTrue( temporalAmounts.contains( Duration.ofMinutes( -10 ) ) );
    }

    @Test
    public void testHours()
    {
        List<TemporalAmount> temporalAmounts = timeParser.parse( "+3h " );

        assertEquals( temporalAmounts.size(), 2 );
        assertTrue( temporalAmounts.contains( Period.ZERO ) );
        assertTrue( temporalAmounts.contains( Duration.ofHours( 3 ) ) );

        temporalAmounts = timeParser.parse( " -3h" );
        assertTrue( temporalAmounts.contains( Duration.ofHours( -3 ) ) );

        temporalAmounts = timeParser.parse( " +1hour " );
        assertTrue( temporalAmounts.contains( Duration.ofHours( 1 ) ) );

        temporalAmounts = timeParser.parse( "  -1hour" );
        assertTrue( temporalAmounts.contains( Duration.ofHours( -1 ) ) );

        temporalAmounts = timeParser.parse( "+10hours  " );
        assertTrue( temporalAmounts.contains( Duration.ofHours( 10 ) ) );

        temporalAmounts = timeParser.parse( " -10 hours " );
        assertTrue( temporalAmounts.contains( Duration.ofHours( -10 ) ) );
    }

    @Test
    public void testTime()
    {
        List<TemporalAmount> temporalAmounts = timeParser.parse( "+3h +2m" );

        assertEquals( temporalAmounts.size(), 2 );
        assertTrue( temporalAmounts.contains( Period.ZERO ) );
        assertTrue( temporalAmounts.contains( Duration.parse( "PT3h2m" ) ) );

        temporalAmounts = timeParser.parse( "-3h+2minutes" );
        assertTrue( temporalAmounts.contains( Duration.parse( "PT-3h2m" ) ) );

        temporalAmounts = timeParser.parse( "+2minutes    -3hours  " );
        assertTrue( temporalAmounts.contains( Duration.parse( "PT-3h2m" ) ) );

        temporalAmounts = timeParser.parse( "+1minutes-3hours +1s" );
        assertTrue( temporalAmounts.contains( Duration.parse( "-PT2h58m59s" ) ) );
    }

    @Test
    public void testDays()
    {
        List<TemporalAmount> temporalAmounts = timeParser.parse( "+3d " );

        assertEquals( temporalAmounts.size(), 2 );
        assertTrue( temporalAmounts.contains( Period.ofDays( 3 ) ) );
        assertTrue( temporalAmounts.contains( Duration.ZERO ) );

        temporalAmounts = timeParser.parse( " -3d" );
        assertTrue( temporalAmounts.contains( Period.ofDays( -3 ) ) );

        temporalAmounts = timeParser.parse( " +1day " );
        assertTrue( temporalAmounts.contains( Period.ofDays( 1 ) ) );

        temporalAmounts = timeParser.parse( "  -1day" );
        assertTrue( temporalAmounts.contains( Period.ofDays( -1 ) ) );

        temporalAmounts = timeParser.parse( "+10days  " );
        assertTrue( temporalAmounts.contains( Period.ofDays( 10 ) ) );

        temporalAmounts = timeParser.parse( " -10 days " );
        assertTrue( temporalAmounts.contains( Period.ofDays( -10 ) ) );
    }

    @Test
    public void testMonths()
    {
        List<TemporalAmount> temporalAmounts = timeParser.parse( "+3M " );

        assertEquals( temporalAmounts.size(), 2 );
        assertTrue( temporalAmounts.contains( Period.ofMonths( 3 ) ) );
        assertTrue( temporalAmounts.contains( Duration.ZERO ) );

        temporalAmounts = timeParser.parse( " -3M" );
        assertTrue( temporalAmounts.contains( Period.ofMonths( -3 ) ) );

        temporalAmounts = timeParser.parse( " +1month " );
        assertTrue( temporalAmounts.contains( Period.ofMonths( 1 ) ) );

        temporalAmounts = timeParser.parse( "  -1month" );
        assertTrue( temporalAmounts.contains( Period.ofMonths( -1 ) ) );

        temporalAmounts = timeParser.parse( "+10months  " );
        assertTrue( temporalAmounts.contains( Period.ofMonths( 10 ) ) );

        temporalAmounts = timeParser.parse( " -10 months " );
        assertTrue( temporalAmounts.contains( Period.ofMonths( -10 ) ) );
    }

    @Test
    public void testYears()
    {
        List<TemporalAmount> temporalAmounts = timeParser.parse( "+3y " );

        assertEquals( temporalAmounts.size(), 2 );
        assertTrue( temporalAmounts.contains( Period.ofYears( 3 ) ) );
        assertTrue( temporalAmounts.contains( Duration.ZERO ) );

        temporalAmounts = timeParser.parse( " -3y" );
        assertTrue( temporalAmounts.contains( Period.ofYears( -3 ) ) );

        temporalAmounts = timeParser.parse( " +1year " );
        assertTrue( temporalAmounts.contains( Period.ofYears( 1 ) ) );

        temporalAmounts = timeParser.parse( "  -1year" );
        assertTrue( temporalAmounts.contains( Period.ofYears( -1 ) ) );

        temporalAmounts = timeParser.parse( "+10years  " );
        assertTrue( temporalAmounts.contains( Period.ofYears( 10 ) ) );

        temporalAmounts = timeParser.parse( " -10 years " );
        assertTrue( temporalAmounts.contains( Period.ofYears( -10 ) ) );
    }

    @Test
    public void testDate()
    {
        List<TemporalAmount> temporalAmounts = timeParser.parse( " +3y +2d " );

        assertEquals( temporalAmounts.size(), 2 );
        assertTrue( temporalAmounts.contains( Duration.ZERO ) );
        assertTrue( temporalAmounts.contains( Period.of( 3, 0, 2 ) ) );

        temporalAmounts = timeParser.parse( "-2years+1month   -24d" );
        assertTrue( temporalAmounts.contains( Period.of( -2, 1, -24 ) ) );

        temporalAmounts = timeParser.parse( "-3M+2d+1y" );
        assertTrue( temporalAmounts.contains( Period.of( 1, -3, 2 ) ) );
    }

    @Test
    public void testDateTime()
    {
        List<TemporalAmount> temporalAmounts = timeParser.parse( " +3M -3m " );

        assertEquals( temporalAmounts.size(), 2 );
        assertTrue( temporalAmounts.contains( Duration.ofMinutes( -3 ) ) );
        assertTrue( temporalAmounts.contains( Period.ofMonths( 3 ) ) );

        temporalAmounts = timeParser.parse( "+2m +3days-1s-2M +23year " );

        assertTrue( temporalAmounts.contains( Duration.parse( "PT2m-1s" ) ) );
        assertTrue( temporalAmounts.contains( Period.of( 23, -2, 3 ) ) );

        temporalAmounts = timeParser.parse( "+2years+1month-12d +3h-1minute+24seconds " );

        assertTrue( temporalAmounts.contains( Duration.parse( "PT3h-1m+24s" ) ) );
        assertTrue( temporalAmounts.contains( Period.of( 2, 1, -12 ) ) );
    }

    @Test
    public void testOverload()
    {
        List<TemporalAmount> temporalAmounts = timeParser.parse( " +1s-1s " );

        assertEquals( temporalAmounts.size(), 2 );
        assertTrue( temporalAmounts.contains( Duration.ofMinutes( 0 ) ) );
        assertTrue( temporalAmounts.contains( Period.ofMonths( 0 ) ) );

        temporalAmounts = timeParser.parse( " +1m-60s " );

        assertTrue( temporalAmounts.contains( Duration.ofMinutes( 0 ) ) );

        temporalAmounts = timeParser.parse( " +61m " );

        assertTrue( temporalAmounts.contains( Duration.parse( "PT1h1m" ) ) );
    }

    @Test
    public void testInvalid()
    {
        List<TemporalAmount> temporalAmounts = timeParser.parse( " +1v " );
        assertNull( temporalAmounts );

        temporalAmounts = timeParser.parse( " +1haur-1sm+1doy " );
        assertNull( temporalAmounts );

        temporalAmounts = timeParser.parse( " +1haur-1sm+1doy +4M-3s" );

        assertEquals( temporalAmounts.size(), 2 );
        assertTrue( temporalAmounts.contains( Period.ofMonths( 4 ) ) );
        assertTrue( temporalAmounts.contains( Duration.ofSeconds( -3 ) ) );


    }
}
