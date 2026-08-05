package com.enonic.nodb.engine.search;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * D8: the collation keys XP-side sorting used to delegate to the engine's analysis-icu.
 *
 * <p>The tests sort the HEX KEYS as plain strings, which is exactly what OpenSearch does to a
 * {@code keyword} field — so a green test here is a statement about the index's sort order, not
 * merely about ICU.
 */
class CollationKeyResolverTest
{
    /** Sort the keys the way a keyword field does, and read back the original values in that order. */
    private static List<String> sortedByKey( String locale, List<String> values )
    {
        List<String> copy = new ArrayList<>( values );
        copy.sort( Comparator.comparing( value -> CollationKeyResolver.collationKey( locale, value ) ) );
        return copy;
    }

    /**
     * Norwegian: æ ø å sort AFTER z, in that order. Gate 0(d) proved the same ordering through a
     * live {@code icu_collation_keyword} — this proves XP-side keys reproduce it.
     */
    @Test
    void norwegianSortsAeOeAaAfterZ()
    {
        assertEquals( List.of( "a", "z", "æ", "ø", "å" ), sortedByKey( "no", List.of( "å", "æ", "z", "ø", "a" ) ) );
    }

    /** And the control: a plain byte/codepoint sort gets it wrong, which is why the keys exist. */
    @Test
    void plainStringSortGetsNorwegianWrong()
    {
        List<String> naive = new ArrayList<>( List.of( "å", "æ", "z", "ø", "a" ) );
        naive.sort( Comparator.naturalOrder() );
        assertEquals( List.of( "a", "z", "å", "æ", "ø" ), naive );
        assertNotEquals( naive, sortedByKey( "no", naive ) );
    }

    /** Swedish: ä and ö come after z, and — unlike Norwegian — å precedes ä. */
    @Test
    void swedishSortsAaAeOeAfterZ()
    {
        assertEquals( List.of( "a", "z", "å", "ä", "ö" ), sortedByKey( "sv", List.of( "ö", "ä", "å", "z", "a" ) ) );
    }

    /** German: ß collates with ss, so "straße" lands between "strasse" and "strauss", not after z. */
    @Test
    void germanSharpSCollatesWithSs()
    {
        assertEquals( List.of( "strasse", "straße", "strauss" ), sortedByKey( "de", List.of( "straße", "strauss", "strasse" ) ) );
    }

    /** Danish differs from Swedish on the same three letters — the reason the locale is in the field name. */
    @Test
    void danishAndSwedishDisagree()
    {
        List<String> values = List.of( "ö", "ä", "å", "z", "a" );
        assertNotEquals( sortedByKey( "da", values ), sortedByKey( "sv", values ) );
    }

    @Test
    void ducetIsTheFallbackForUnknownAndAbsentLocales()
    {
        String ducet = CollationKeyResolver.collationKey( CollationKeyResolver.DUCET, "æble" );
        assertEquals( ducet, CollationKeyResolver.collationKey( "zzz-not-a-language", "æble" ) );
        assertEquals( ducet, CollationKeyResolver.collationKey( null, "æble" ) );
    }

    @Test
    void keysAreLowercaseHexAndStableAcrossCalls()
    {
        String key = CollationKeyResolver.collationKey( "no", "Ærlig" );
        assertTrue( key.matches( "[0-9a-f]+" ), key );
        assertEquals( 0, key.length() % 2, "hex must be whole bytes" );
        assertEquals( key, CollationKeyResolver.collationKey( "no", "Ærlig" ) );
    }

    @Test
    void nullValueYieldsNoKey()
    {
        assertNull( CollationKeyResolver.collationKey( "no", null ) );
    }

    /**
     * Prefix-safe truncation: an over-long value is capped at a whole-byte boundary, and two values
     * that agree on the capped prefix therefore compare EQUAL rather than in some arbitrary order —
     * coarser, never wrong.
     */
    @Test
    void overLongValuesAreTruncatedAtAByteBoundaryAndStayPrefixSafe()
    {
        String longValue = "æ".repeat( 20_000 );
        String key = CollationKeyResolver.collationKey( "no", longValue );
        assertEquals( CollationKeyResolver.MAX_HEX_LENGTH, key.length() );
        assertEquals( 0, key.length() % 2 );
        assertEquals( key, CollationKeyResolver.collationKey( "no", longValue + "zzz" ) );
    }

    /**
     * {@link java.text.Collator} is documented as not thread-safe; the resolver caches FROZEN ICU
     * collators, which are. Run the same values from many threads and require identical keys — the
     * indexer's hot path is concurrent by construction.
     */
    @Test
    void frozenCollatorsAreSafeUnderConcurrency()
        throws Exception
    {
        List<String> values = List.of( "æble", "øl", "ås", "straße", "Ωμέγα" );
        String[] locales = {"no", "sv", "de", "el", "ducet"};

        List<String> expected = new ArrayList<>();
        for ( String locale : locales )
        {
            for ( String value : values )
            {
                expected.add( CollationKeyResolver.collationKey( locale, value ) );
            }
        }

        try (ExecutorService pool = Executors.newFixedThreadPool( 8 ))
        {
            List<Callable<List<String>>> tasks = new ArrayList<>();
            for ( int i = 0; i < 64; i++ )
            {
                tasks.add( () -> {
                    List<String> keys = new ArrayList<>();
                    for ( String locale : locales )
                    {
                        for ( String value : values )
                        {
                            keys.add( CollationKeyResolver.collationKey( locale, value ) );
                        }
                    }
                    return keys;
                } );
            }
            for ( Future<List<String>> future : pool.invokeAll( tasks ) )
            {
                assertEquals( expected, future.get() );
            }
        }
    }
}
