package com.enonic.nodb.engine.search;

import java.util.HexFormat;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ibm.icu.text.CollationKey;
import com.ibm.icu.text.Collator;
import com.ibm.icu.util.ULocale;

/**
 * Language-aware sort keys for the {@code *._orderby_<loc>} sub-fields (BUILD-PHASE-4.md D8).
 *
 * <p><b>The change.</b> ES 2.4 mapped those 44 fields as {@code type:string,
 * index:analyzed, analyzer:icu_sort_<loc>} and sorted on them directly — legal only because
 * fielddata was on by default — so the sort contract was delegated to the ENGINE's ICU. Here
 * the key is computed instead and stored as a plain {@code keyword}: language sorts are
 * treated exactly like numeric and date sorts, which XP has always pre-encoded
 * ({@code OrderByValueResolver}/{@code LexiSortable} — numbers to 17-char hex, dates to a
 * fixed UTC pattern). Consequences: no fielddata, no {@code icu_collation_keyword}, and
 * {@code analysis-icu} is needed for NOTHING — the 44 {@code icu_collation} filters were its
 * only consumer, so the stack runs the STOCK {@code opensearchproject/opensearch:3.7.0} image
 * with no derived image and no managed-service plugin-parity risk. 44 filters + 44 analyzers
 * are deleted and 44 dynamic templates collapse into one.
 *
 * <p><b>Faithful by construction.</b> XP's collation filters set no options whatsoever —
 * every one is literally {@code {"type":"icu_collation","language":"<code>"}}, with no
 * strength, alternate, caseFirst, numeric or decomposition — so
 * {@code Collator.getInstance(locale).getCollationKey(text)} reproduces them with no option
 * matrix to replicate. The INPUT is equally faithful: XP hands the same
 * {@code OrderByValueResolver} output (already {@code toLowerCase()}d and truncated at 1024
 * chars) to {@code _orderby} and to every {@code _orderby_<loc>} variant, and ES 2.4's
 * {@code icu_sort_<loc>} chain is {@code tokenizer: keyword} + the collation filter — i.e. it
 * collated exactly that already-normalized string. Collating it here is the same operation in
 * a different place.
 *
 * <p><b>Where it runs, and why not in XP.</b> D8's recommendation put a
 * {@code CollationKeyResolver} beside {@code OrderByValueResolver} in core-repo with a call
 * site in {@code IndexItemFactory.createOrderBy}. That call site is unconditional, and D8's
 * own scope rule is "nodb path only — changing {@code _orderby_<loc>} for embedded ES would
 * alter that layout and force a reindex", so wiring it there would either break the
 * byte-identical rule or need a mode flag inside core-repo (which has none: backend selection
 * is OSGi service ranking, invisible above the SPI). The remaining XP-side home would be the
 * nodb client bundle, which embeds its dependencies as private packages — a 14 MB icu4j embed.
 * So the computation lives HERE, with icu4j pinned in nodb's own version catalog at the same
 * 78.3 XP pins. D8's stated risk was delegating the contract to the engine's OLDER ICU (77.1
 * in the image); an explicitly pinned 78.3 under NoDB's control removes it identically — and
 * NoDB is the component that owns index generations, which is precisely who must pin an ICU
 * version whose bump requires a {@code +g(N+1)} rebuild rather than an in-place change
 * (Gate 0(d) decision 5). It also survives decision 3's later swap to server-side document
 * derivation unchanged.
 *
 * <p><b>Thread safety.</b> {@link Collator} is documented as not thread-safe. A frozen
 * Collator, however, is: {@link Collator#freeze()} makes the instance immutable and safe for
 * concurrent use, which is strictly better than cloning per call on the indexer's hot path.
 * One frozen instance per locale is cached.
 */
public final class CollationKeyResolver
{
    /**
     * Prefix-safe cap on the hex key, in characters — 4096 collation-key bytes.
     *
     * <p>Two properties make truncation safe. (1) ICU collation keys compare bytewise
     * ({@code memcmp}), so a byte-prefix of a key is itself a valid, order-preserving prefix:
     * two values whose keys agree on the first 4096 bytes are treated as equal for sorting
     * instead of being ordered wrongly. (2) The cut is at an EVEN hex index, so it can never
     * split a byte into two half-characters — which would corrupt the comparison rather than
     * merely coarsen it.
     *
     * <p>Sized against the source: {@code OrderByValueResolver} already truncates at 1024
     * chars and a collation key runs roughly 2–4 bytes per char, so 4096 bytes covers the
     * realistic worst case; the template's {@code ignore_above: 8192} sits above this cap so
     * the index never silently drops a key that this resolver produced.
     */
    static final int MAX_HEX_LENGTH = 8192;

    /** DUCET (the Unicode default collation) — the fallback for any unmapped language. */
    public static final String DUCET = "ducet";

    private static final Map<String, Collator> COLLATORS = new ConcurrentHashMap<>();

    private CollationKeyResolver()
    {
    }

    /**
     * The hex-encoded ICU collation key for {@code value} under {@code localeCode}, or
     * {@code null} for a null input (a property with no value has nothing to sort by).
     *
     * <p>{@code localeCode} is the code XP already put in the field name — the {@code <loc>}
     * of {@code _orderby_<loc>}, e.g. {@code no}, {@code sv}, {@code de}, or {@code ducet}.
     * Note that XP normalizes Norwegian to the single code {@code no} for both {@code nb} and
     * {@code nn} (its {@code IndexLanguageController} does the {@code no}→{@code nb} base
     * normalization and then emits {@code no}), so this resolver never sees {@code nb}/{@code nn}
     * from the document path — it accepts them anyway, since ICU resolves both.
     */
    public static String collationKey( String localeCode, String value )
    {
        if ( value == null )
        {
            return null;
        }
        CollationKey key = collator( localeCode ).getCollationKey( value );
        String hex = HexFormat.of().formatHex( key.toByteArray() );
        return hex.length() <= MAX_HEX_LENGTH ? hex : hex.substring( 0, MAX_HEX_LENGTH );
    }

    /**
     * Frozen, cached, concurrency-safe Collator for a locale code. An unknown code falls back
     * to DUCET rather than throwing: the code came off a field name in a document XP built,
     * and refusing to index a node because of an exotic language tag would be a worse failure
     * than sorting it by the Unicode default (which is exactly what XP's own
     * {@code IndexLanguageController} does for unmapped languages).
     */
    private static Collator collator( String localeCode )
    {
        return COLLATORS.computeIfAbsent( localeCode == null ? DUCET : localeCode, code -> {
            Collator collator = DUCET.equals( code ) ? Collator.getInstance( ULocale.ROOT ) : Collator.getInstance( toLocale( code ) );
            collator.freeze();
            return collator;
        } );
    }

    private static ULocale toLocale( String code )
    {
        // Field-name codes are BCP-47-ish ("no", "pt-br"); Locale.forLanguageTag handles both
        // and yields ROOT (-> DUCET behaviour) for anything unparseable.
        Locale locale = Locale.forLanguageTag( code );
        return locale.getLanguage().isEmpty() ? ULocale.ROOT : ULocale.forLocale( locale );
    }
}
