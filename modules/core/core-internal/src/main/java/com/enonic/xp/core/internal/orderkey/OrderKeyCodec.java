package com.enonic.xp.core.internal.orderkey;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Random;
import java.util.random.RandomGenerator;

/**
 * Mints and manipulates dense order keys. A key is {@code position '.' discriminator}: the position is a base-62 fraction
 * whose digits sort lexicographically in value order, and the discriminator - the id of the node holding the key - makes
 * two live keys unequal by construction, so independently minted keys never collide when sibling sets from different
 * branches or layers merge. The separator sorts below every digit, which keeps a position that extends another sorting
 * after it, the way the longer fraction is the larger number.
 * <p>
 * A birth position encodes the inverted creation instant, so a fresh key sorts before every older one under ascending
 * order: creation and move-to-first are the same operation and need no reads. The relative operations take the anchor
 * keys the caller already holds and never look at the rest of the sibling set; a position between two others always
 * exists, so no operation renumbers existing keys.
 * <p>
 * Instances are as thread-safe as the generator they are given.
 */
public final class OrderKeyCodec
{
    static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    static final char SEPARATOR = '.';

    static final int MAX_POSITION_LENGTH = 512;

    static final int MAX_DISCRIMINATOR_LENGTH = 64;

    private static final int RADIX = 62;

    private static final BigInteger RADIX_BIG = BigInteger.valueOf( RADIX );

    private static final int INSTANT_DIGITS = 6;

    private static final int JITTER_DIGITS = 3;

    /**
     * 62^6 - 1. Instants up to this many epoch seconds (year 3769) fit the instant digits.
     */
    private static final long MAX_SECONDS = 56_800_235_583L;

    private static final int JITTER_BOUND = RADIX * RADIX * RADIX;

    /**
     * Steps and picks never work at a scale coarser than a birth position, whatever length canonical stripping left the
     * anchor with: a digit of scale is precision, not magnitude, and a step taken at the length of a stripped position
     * would be coarser than the space it moves in.
     */
    private static final int MIN_SCALE = INSTANT_DIGITS + JITTER_DIGITS;

    /**
     * A relative step stays within this many units of the last digit of the anchor, so a run of steps in one direction
     * consumes the space linearly instead of halving it.
     */
    private static final int STEP_BOUND = 8;

    private static final int[] DIGIT_VALUES = new int[128];

    static
    {
        java.util.Arrays.fill( DIGIT_VALUES, -1 );
        for ( int i = 0; i < ALPHABET.length(); i++ )
        {
            DIGIT_VALUES[ALPHABET.charAt( i )] = i;
        }
    }

    private final RandomGenerator random;

    private final Random randomAdapter;

    public OrderKeyCodec( final RandomGenerator random )
    {
        this.random = random;
        this.randomAdapter = Random.from( random );
    }

    public String initial( final Instant instant, final String discriminator )
    {
        requireValidDiscriminator( discriminator );
        final long seconds = instant.getEpochSecond();
        if ( seconds < 0 || seconds >= MAX_SECONDS )
        {
            throw new IllegalArgumentException( "Instant out of order key range: " + instant );
        }

        final StringBuilder position = new StringBuilder( INSTANT_DIGITS + JITTER_DIGITS );
        appendDigits( position, MAX_SECONDS - seconds, INSTANT_DIGITS );
        appendDigits( position, random.nextInt( JITTER_BOUND ), JITTER_DIGITS );

        return key( canonical( position.toString() ), discriminator );
    }

    public String between( final String keyA, final String keyB, final String discriminator )
    {
        requireValidDiscriminator( discriminator );
        final String positionA = positionOf( keyA );
        final String positionB = positionOf( keyB );
        if ( keyA.compareTo( keyB ) >= 0 )
        {
            throw new IllegalArgumentException( "Anchors out of order" );
        }

        if ( positionA.equals( positionB ) )
        {
            // Two keys share a position only when they differ by discriminator alone, and no third discriminator can be
            // chosen to land between them. The new key extends the shared position instead, which places it directly
            // after both anchors - adjacent to where it was asked to go.
            return key( canonical( positionA + ALPHABET.charAt( 1 + random.nextInt( RADIX - 1 ) ) ), discriminator );
        }

        final int full = Math.max( positionA.length(), positionB.length() ) + 1;
        final BigInteger valueA = valueAt( positionA, full );
        final BigInteger valueB = valueAt( positionB, full );

        // The pick is made at the coarsest scale whose floors already leave room, so a wide gap yields a short position
        // and the length of a key reflects how contended its spot is, not how deep its anchors happened to be.
        int scale = commonPrefixLength( positionA, positionB ) + 1;
        BigInteger floorA;
        BigInteger floorB;
        while ( true )
        {
            final BigInteger unit = RADIX_BIG.pow( full - scale );
            floorA = valueA.divide( unit );
            floorB = valueB.divide( unit );
            if ( floorB.subtract( floorA ).compareTo( BigInteger.TWO ) >= 0 )
            {
                break;
            }
            scale++;
        }

        final BigInteger gap = floorB.subtract( floorA ).subtract( BigInteger.ONE );
        final BigInteger picked = floorA.add( BigInteger.ONE ).add( uniform( gap ) );

        return key( canonical( render( picked, scale ) ), discriminator );
    }

    public String after( final String key, final String discriminator )
    {
        requireValidDiscriminator( discriminator );
        final String position = positionOf( key );

        int scale = Math.max( position.length(), MIN_SCALE );
        BigInteger value = valueAt( position, scale );
        final BigInteger step = BigInteger.valueOf( 1 + random.nextInt( STEP_BOUND ) );

        BigInteger stepped = value.add( step );
        while ( stepped.compareTo( RADIX_BIG.pow( scale ) ) >= 0 )
        {
            scale++;
            value = value.multiply( RADIX_BIG );
            stepped = value.add( step );
        }

        return key( canonical( render( stepped, scale ) ), discriminator );
    }

    public String before( final String key, final String discriminator )
    {
        requireValidDiscriminator( discriminator );
        final String position = positionOf( key );

        int scale = Math.max( position.length(), MIN_SCALE );
        BigInteger value = valueAt( position, scale );
        final BigInteger step = BigInteger.valueOf( 1 + random.nextInt( STEP_BOUND ) );

        while ( value.compareTo( step ) <= 0 )
        {
            scale++;
            value = value.multiply( RADIX_BIG );
        }

        return key( canonical( render( value.subtract( step ), scale ) ), discriminator );
    }

    /**
     * Validates a key of unknown origin, returning it unchanged. The position must be canonical - no trailing zero
     * digit, so that the space directly after it stays mintable - and both parts must fit the size caps that keep a
     * hostile key from growing the index.
     */
    public static String requireValidKey( final String key )
    {
        final int separator = key.indexOf( SEPARATOR );
        if ( separator < 1 )
        {
            throw new IllegalArgumentException( "Order key without position" );
        }
        positionOf( key );
        requireValidDiscriminator( key.substring( separator + 1 ) );
        return key;
    }

    private static String positionOf( final String key )
    {
        final int separator = key.indexOf( SEPARATOR );
        if ( separator < 1 )
        {
            throw new IllegalArgumentException( "Order key without position" );
        }
        final String position = key.substring( 0, separator );
        if ( position.length() > MAX_POSITION_LENGTH )
        {
            throw new IllegalArgumentException( "Order key position too long: " + position.length() );
        }
        for ( int i = 0; i < position.length(); i++ )
        {
            final char c = position.charAt( i );
            if ( c >= DIGIT_VALUES.length || DIGIT_VALUES[c] < 0 )
            {
                throw new IllegalArgumentException( "Invalid order key digit: " + c );
            }
        }
        if ( position.charAt( position.length() - 1 ) == ALPHABET.charAt( 0 ) )
        {
            throw new IllegalArgumentException( "Order key position not canonical" );
        }
        return position;
    }

    private static void requireValidDiscriminator( final String discriminator )
    {
        if ( discriminator == null || discriminator.isEmpty() || discriminator.length() > MAX_DISCRIMINATOR_LENGTH )
        {
            throw new IllegalArgumentException( "Invalid order key discriminator" );
        }
    }

    private static String key( final String position, final String discriminator )
    {
        return position + SEPARATOR + discriminator;
    }

    private static String canonical( final String position )
    {
        int end = position.length();
        while ( end > 0 && position.charAt( end - 1 ) == ALPHABET.charAt( 0 ) )
        {
            end--;
        }
        if ( end == 0 )
        {
            throw new IllegalArgumentException( "Order key position collapsed to zero" );
        }
        if ( end > MAX_POSITION_LENGTH )
        {
            throw new IllegalStateException( "Order key space exhausted at this spot" );
        }
        return position.substring( 0, end );
    }

    private static BigInteger valueAt( final String position, final int scale )
    {
        BigInteger value = BigInteger.ZERO;
        for ( int i = 0; i < position.length(); i++ )
        {
            value = value.multiply( RADIX_BIG ).add( BigInteger.valueOf( DIGIT_VALUES[position.charAt( i )] ) );
        }
        return value.multiply( RADIX_BIG.pow( scale - position.length() ) );
    }

    private static String render( final BigInteger value, final int scale )
    {
        final char[] digits = new char[scale];
        BigInteger remaining = value;
        for ( int i = scale - 1; i >= 0; i-- )
        {
            final BigInteger[] divmod = remaining.divideAndRemainder( RADIX_BIG );
            digits[i] = ALPHABET.charAt( divmod[1].intValue() );
            remaining = divmod[0];
        }
        if ( remaining.signum() != 0 )
        {
            throw new IllegalStateException( "Order key position overflow" );
        }
        return new String( digits );
    }

    private BigInteger uniform( final BigInteger bound )
    {
        if ( bound.signum() <= 0 )
        {
            return BigInteger.ZERO;
        }
        final int bits = bound.bitLength();
        BigInteger picked;
        do
        {
            picked = new BigInteger( bits, randomAdapter );
        }
        while ( picked.compareTo( bound ) >= 0 );
        return picked;
    }

    private static int commonPrefixLength( final String positionA, final String positionB )
    {
        final int limit = Math.min( positionA.length(), positionB.length() );
        int i = 0;
        while ( i < limit && positionA.charAt( i ) == positionB.charAt( i ) )
        {
            i++;
        }
        return i;
    }

    private static void appendDigits( final StringBuilder builder, final long value, final int count )
    {
        long remaining = value;
        final int offset = builder.length();
        builder.setLength( offset + count );
        for ( int i = count - 1; i >= 0; i-- )
        {
            builder.setCharAt( offset + i, ALPHABET.charAt( (int) ( remaining % RADIX ) ) );
            remaining /= RADIX;
        }
    }
}
