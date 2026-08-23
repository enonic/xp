package com.enonic.xp.core.internal.orderkey;

import java.time.Instant;
import java.util.SplittableRandom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderKeyCodecTest
{
    private static final Instant T0 = Instant.parse( "2026-08-23T12:00:00Z" );

    private final OrderKeyCodec codec = new OrderKeyCodec( new SplittableRandom( 42 ) );

    @Test
    void alphabet_matches_string_order()
    {
        for ( int i = 1; i < OrderKeyCodec.ALPHABET.length(); i++ )
        {
            assertTrue( OrderKeyCodec.ALPHABET.charAt( i - 1 ) < OrderKeyCodec.ALPHABET.charAt( i ) );
        }
        assertTrue( OrderKeyCodec.SEPARATOR < OrderKeyCodec.ALPHABET.charAt( 0 ) );
    }

    @Test
    void initial_sorts_newest_first()
    {
        String previous = codec.initial( T0, "a" );
        for ( int i = 1; i <= 1000; i++ )
        {
            final String next = codec.initial( T0.plusSeconds( i ), "a" );
            assertTrue( next.compareTo( previous ) < 0, "later instant must sort first" );
            previous = next;
        }
    }

    @Test
    void same_instant_same_jitter_still_distinct_keys()
    {
        final String keyA = new OrderKeyCodec( new SplittableRandom( 7 ) ).initial( T0, "node-a" );
        final String keyB = new OrderKeyCodec( new SplittableRandom( 7 ) ).initial( T0, "node-b" );
        assertEquals( positionOf( keyA ), positionOf( keyB ), "identical seed and instant collide on position" );
        assertNotEquals( keyA, keyB, "discriminator keeps full keys distinct" );
    }

    @Test
    void between_stays_strictly_between_under_random_nesting()
    {
        String lo = codec.initial( T0.plusSeconds( 3600 ), "lo" );
        String hi = codec.initial( T0, "hi" );
        final SplittableRandom sides = new SplittableRandom( 1 );
        for ( int i = 0; i < 1500; i++ )
        {
            final String picked = codec.between( lo, hi, "n" + i );
            assertTrue( lo.compareTo( picked ) < 0, "not above lower anchor at step " + i );
            assertTrue( picked.compareTo( hi ) < 0, "not below upper anchor at step " + i );
            assertDoesNotThrow( () -> OrderKeyCodec.requireValidKey( picked ) );
            if ( sides.nextBoolean() )
            {
                lo = picked;
            }
            else
            {
                hi = picked;
            }
        }
    }

    @Test
    void adversarial_nesting_survives_hundreds_of_levels_then_fails_clean()
    {
        String lo = codec.initial( T0.plusSeconds( 3600 ), "lo" );
        String hi = codec.initial( T0, "hi" );
        int depth = 0;
        try
        {
            for ( ; depth < 4000; depth++ )
            {
                hi = codec.between( lo, hi, "n" + depth );
            }
        }
        catch ( IllegalStateException e )
        {
            // the clean refusal once the capped position length is exhausted at one spot
        }
        assertTrue( depth >= 1000, "expected at least 1000 nested inserts into one gap, got " + depth );
    }

    @Test
    void after_run_keeps_length_stable()
    {
        String key = codec.initial( T0, "n" );
        final int initialLength = positionOf( key ).length();
        for ( int i = 0; i < 10_000; i++ )
        {
            final String next = codec.after( key, "n" );
            assertTrue( next.compareTo( key ) > 0, "after must sort after, step " + i );
            key = next;
        }
        assertTrue( positionOf( key ).length() <= initialLength + 1,
                    "a run in one direction must consume space linearly, not by length" );
    }

    @Test
    void before_run_keeps_length_stable()
    {
        String key = codec.initial( T0, "n" );
        final int initialLength = positionOf( key ).length();
        for ( int i = 0; i < 10_000; i++ )
        {
            final String next = codec.before( key, "n" );
            assertTrue( next.compareTo( key ) < 0, "before must sort before, step " + i );
            key = next;
        }
        assertTrue( positionOf( key ).length() <= initialLength + 1 );
    }

    @Test
    void before_near_zero_scales_instead_of_underflowing()
    {
        String key = "1.n";
        for ( int i = 0; i < 100; i++ )
        {
            final String next = codec.before( key, "n" );
            assertTrue( next.compareTo( key ) < 0 );
            assertDoesNotThrow( () -> OrderKeyCodec.requireValidKey( next ) );
            key = next;
        }
    }

    @Test
    void between_equal_positions_lands_directly_after_the_anchors()
    {
        final String keyA = "AB3.node-1";
        final String keyB = "AB3.node-2";
        final String picked = codec.between( keyA, keyB, "node-3" );
        assertTrue( picked.compareTo( keyB ) > 0, "no key fits between equal positions; adjacency is the contract" );
        assertDoesNotThrow( () -> OrderKeyCodec.requireValidKey( picked ) );
    }

    @Test
    void independent_writers_never_collide_on_the_same_anchors()
    {
        final String lo = codec.initial( T0.plusSeconds( 60 ), "lo" );
        final String hi = codec.initial( T0, "hi" );
        final OrderKeyCodec writerA = new OrderKeyCodec( new SplittableRandom( 5 ) );
        final OrderKeyCodec writerB = new OrderKeyCodec( new SplittableRandom( 5 ) );
        final String keyA = writerA.between( lo, hi, "node-a" );
        final String keyB = writerB.between( lo, hi, "node-b" );
        assertEquals( positionOf( keyA ), positionOf( keyB ), "identical seeds collide on position" );
        assertNotEquals( keyA, keyB );
        assertTrue( lo.compareTo( keyA ) < 0 && keyA.compareTo( hi ) < 0 );
        assertTrue( lo.compareTo( keyB ) < 0 && keyB.compareTo( hi ) < 0 );
    }

    @Test
    void anchors_out_of_order_are_rejected()
    {
        final String newer = codec.initial( T0.plusSeconds( 60 ), "a" );
        final String older = codec.initial( T0, "b" );
        assertThrows( IllegalArgumentException.class, () -> codec.between( older, newer, "c" ) );
    }

    @Test
    void validation_rejects_malformed_keys()
    {
        assertThrows( IllegalArgumentException.class, () -> OrderKeyCodec.requireValidKey( "A0.x" ) );
        assertThrows( IllegalArgumentException.class, () -> OrderKeyCodec.requireValidKey( "A" ) );
        assertThrows( IllegalArgumentException.class, () -> OrderKeyCodec.requireValidKey( ".x" ) );
        assertThrows( IllegalArgumentException.class, () -> OrderKeyCodec.requireValidKey( "A!.x" ) );
        assertThrows( IllegalArgumentException.class, () -> OrderKeyCodec.requireValidKey( "A." ) );
        assertThrows( IllegalArgumentException.class,
                      () -> OrderKeyCodec.requireValidKey( "A".repeat( 512 ) + "1.x" ) );
        assertThrows( IllegalArgumentException.class, () -> OrderKeyCodec.requireValidKey( "A1." + "x".repeat( 65 ) ) );
        assertDoesNotThrow( () -> OrderKeyCodec.requireValidKey( "A1.x" ) );
        assertDoesNotThrow( () -> OrderKeyCodec.requireValidKey( codec.initial( T0, "node-id" ) ) );
    }

    @Test
    void instants_outside_the_encodable_range_are_rejected()
    {
        assertThrows( IllegalArgumentException.class, () -> codec.initial( Instant.ofEpochSecond( -1 ), "a" ) );
        assertThrows( IllegalArgumentException.class, () -> codec.initial( Instant.parse( "4000-01-01T00:00:00Z" ), "a" ) );
    }

    private static String positionOf( final String key )
    {
        return key.substring( 0, key.indexOf( OrderKeyCodec.SEPARATOR ) );
    }
}
