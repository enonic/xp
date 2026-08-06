package com.enonic.nodb.engine.search;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Highlighting, both directions: XP's canonical highlight config → an OpenSearch {@code highlight}
 * block, and a hit's {@code highlight} section → the canonical property names XP expects back.
 *
 * <h2>The three-field expansion, and the naming trap that turned out not to be one</h2>
 * XP highlights each requested property on THREE fields, because a property is indexed three ways
 * and a match can land on any of them: the plain value, the analyzed variant and the edge-ngram
 * variant. The Gate 0(c) inventory recorded these as {@code name_analyzed}/{@code name_ngram} —
 * "underscore, no dot" — and warned that highlighting would silently return nothing if that
 * disagreed with the {@code *._analyzed} templates. <b>Measured: the inventory misread it.</b>
 * {@code IndexValueType.INDEX_VALUE_TYPE_SEPARATOR} is {@code "."} and the postfix constants
 * themselves begin with an underscore, so
 * {@code ElasticHighlightQueryBuilderFactory} emits {@code title}, {@code title._analyzed},
 * {@code title._ngram} — dotted, consistent with the templates, and asserted as such by XP's own
 * {@code ElasticHighlightQueryBuilderFactoryTest}. There was never a mismatch to fix.
 * <p>
 * What DOES change here is the nodb path's own vocabulary: the bare variant becomes
 * {@code title._text} (D1 — it has to, since the bare name is an object in OpenSearch) and the
 * analyzed variant becomes {@code title._fulltext} (D1b). So the three physical fields are
 * {@code title._text}, {@code title._fulltext}, {@code title._ngram}, and the response side strips
 * all three postfixes back to {@code title}. Both directions read {@link #POSTFIXES}, so they
 * cannot drift apart.
 *
 * <h2>{@code require_field_match}: sent explicitly, always</h2>
 * OpenSearch flipped this default from {@code false} to {@code true}. Under {@code true} the
 * highlighter only highlights the field the QUERY matched — so a {@code fulltext()} query, which
 * matches {@code description._fulltext}, would highlight that field and silently stop highlighting
 * {@code description._text} and {@code description._ngram}. The three-field expansion exists
 * precisely to catch a match wherever it lands, so inheriting the new default would quietly
 * dismantle it.
 * <p>
 * XP only sends the parameter when its value is non-null — and its Java builder defaults it to
 * {@code false}, so in practice it is nearly always sent as {@code false}, while the script layer
 * leaves it null when the key is absent. Rather than reproduce that split (which would make the
 * behaviour depend on which API the caller came through), this translator sends
 * {@code require_field_match} EXPLICITLY on every block and every field, defaulting to
 * {@code false} — ES 2.4's default, i.e. the behaviour the corpus recorded. An explicit value is
 * also the only form that is robust against the default moving again.
 */
final class HighlightTranslator
{
    /** Forced unconditionally, exactly as {@code SearchRequestBuilderFactory} does. */
    private static final String HIGHLIGHTER_TYPE = "plain";

    /**
     * The physical postfixes a highlight response may carry, LONGEST-DISAMBIGUATING FIRST so a
     * name is stripped once and correctly. Also the expansion order, which is XP's: bare,
     * analyzed, ngram.
     */
    static final List<String> POSTFIXES =
        List.of( "." + IndexFields.TEXT_POSTFIX, "." + IndexFields.FULLTEXT_POSTFIX, "._ngram" );

    /**
     * Canonical (XP) setting name → OpenSearch parameter name. XP's API vocabulary is not the
     * engine's ({@code numOfFragments} vs {@code number_of_fragments}), and the wire carries XP's
     * — the same rule the rest of this package follows: the client is a serializer, the engine's
     * spelling is the server's business.
     */
    private static final Map<String, String> SETTINGS = Map.ofEntries( Map.entry( "fragmenter", "fragmenter" ),
                                                                       Map.entry( "fragmentSize", "fragment_size" ),
                                                                       Map.entry( "noMatchSize", "no_match_size" ),
                                                                       Map.entry( "numOfFragments", "number_of_fragments" ),
                                                                       Map.entry( "order", "order" ),
                                                                       Map.entry( "preTags", "pre_tags" ),
                                                                       Map.entry( "postTags", "post_tags" ) );

    /** Global-only settings: XP's {@code HighlightQuerySettings} adds exactly these two. */
    private static final Map<String, String> GLOBAL_SETTINGS =
        Map.of( "encoder", "encoder", "tagsSchema", "tags_schema" );

    private static final String REQUIRE_FIELD_MATCH = "requireFieldMatch";

    private HighlightTranslator()
    {
    }

    /**
     * @param highlight canonical config: {@code {settings: {...}, properties: [{name, settings}]}}
     * @return the {@code highlight} block, or {@code null} when no property was requested
     */
    static ObjectNode translate( JsonNode highlight )
    {
        JsonNode properties = highlight.get( "properties" );
        if ( properties == null || !properties.isArray() || properties.isEmpty() )
        {
            // XP's HighlightQuery.empty() reaches the ES path too and produces a highlight block
            // with no fields, which the engine simply ignores. Dropping it is the same result with
            // one less thing on the wire.
            return null;
        }

        ObjectNode body = OpenSearchClient.mapper().createObjectNode();
        body.put( "type", HIGHLIGHTER_TYPE );

        JsonNode settings = highlight.get( "settings" );
        applySettings( body, settings, true );

        ObjectNode fields = OpenSearchClient.mapper().createObjectNode();
        for ( JsonNode property : properties )
        {
            String name = property.path( "name" ).asText( null );
            if ( name == null || name.isBlank() )
            {
                throw new QueryDslTranslator.UnsupportedQueryException( "A highlighted property needs a 'name'" );
            }
            String logical = name.trim().toLowerCase( Locale.ROOT );

            for ( String postfix : POSTFIXES )
            {
                ObjectNode field = OpenSearchClient.mapper().createObjectNode();
                applySettings( field, property.get( "settings" ), false );
                fields.set( logical + postfix, field );
            }
        }
        body.set( "fields", fields );

        return body;
    }

    private static void applySettings( ObjectNode target, JsonNode settings, boolean global )
    {
        for ( Map.Entry<String, String> entry : SETTINGS.entrySet() )
        {
            JsonNode value = settings == null ? null : settings.get( entry.getKey() );
            if ( value != null && !value.isNull() && !( value.isArray() && value.isEmpty() ) )
            {
                target.set( entry.getValue(), value );
            }
        }
        if ( global )
        {
            for ( Map.Entry<String, String> entry : GLOBAL_SETTINGS.entrySet() )
            {
                JsonNode value = settings == null ? null : settings.get( entry.getKey() );
                if ( value != null && !value.isNull() )
                {
                    target.set( entry.getValue(), value );
                }
            }
        }

        JsonNode requireFieldMatch = settings == null ? null : settings.get( REQUIRE_FIELD_MATCH );
        target.put( "require_field_match",
                    requireFieldMatch != null && !requireFieldMatch.isNull() && requireFieldMatch.asBoolean() );
    }

    /**
     * Which expanded variant's fragments win when a property matched on more than one, highest
     * precedence first. See {@link #decode} for why there has to be a winner rather than a merge.
     *
     * <p>{@code ._fulltext} first because it is the variant holding the property's ORIGINAL text:
     * XP normalizes the string variant at index time, so {@code title._text} carries a lower-cased
     * copy and highlights {@code <em>oslo</em>} where {@code title._fulltext} highlights
     * {@code <em>Oslo</em>}. {@code ._ngram} holds the original text too and normally produces the
     * same fragment; {@code ._text} is last because its fragment is an artifact of the keyword
     * normalizer rather than of the document.
     */
    private static final List<String> DECODE_PRECEDENCE =
        List.of( "." + IndexFields.FULLTEXT_POSTFIX, "._ngram", "." + IndexFields.TEXT_POSTFIX );

    /**
     * A hit's {@code highlight} section → canonical properties, postfix stripped.
     *
     * <p>The three expanded fields collapse back onto ONE property name, so when more than one
     * matched something has to give — and it cannot be a merge. XP's own
     * {@code HighlightedProperties} is a name-keyed map whose {@code add} is a {@code put}, so on
     * the ES path whichever field the response iterated LAST silently replaced the others. That
     * discards fragments, and it does so over a {@code HashMap}, i.e. by hash order of the field
     * names.
     *
     * <p>Merging looked like the strict improvement and is not: the variants do NOT agree on the
     * fragment text (corpus row {@code HIGHLIGHT-01} highlights {@code <em>Oslo</em>} on the
     * analyzed variant and {@code <em>oslo</em>} on the normalized keyword one), so a merge returns
     * two fragments for a property that has one match. So one variant still wins — but by
     * {@link #DECODE_PRECEDENCE} rather than by hash order, which makes the result deterministic
     * AND picks the variant carrying the document's own text instead of a normalization artifact.
     */
    static Map<String, List<String>> decode( JsonNode hit )
    {
        JsonNode highlight = hit.get( "highlight" );
        if ( highlight == null || !highlight.isObject() )
        {
            return Map.of();
        }

        Map<String, Integer> winningRank = new LinkedHashMap<>();
        Map<String, List<String>> result = new LinkedHashMap<>();

        highlight.properties().forEach( entry -> {
            String name = canonicalName( entry.getKey() );
            int rank = rank( entry.getKey() );
            Integer current = winningRank.get( name );
            if ( current != null && current <= rank )
            {
                return;
            }

            Set<String> fragments = new LinkedHashSet<>();
            for ( JsonNode fragment : entry.getValue() )
            {
                fragments.add( fragment.asText() );
            }
            winningRank.put( name, rank );
            result.put( name, List.copyOf( fragments ) );
        } );

        return result;
    }

    private static int rank( String physicalName )
    {
        for ( int i = 0; i < DECODE_PRECEDENCE.size(); i++ )
        {
            if ( physicalName.endsWith( DECODE_PRECEDENCE.get( i ) ) )
            {
                return i;
            }
        }
        // A name carrying no known postfix is not an expanded variant at all, so it competes with
        // nothing and must not be displaced by one.
        return -1;
    }

    /** {@code title._fulltext} → {@code title}; a name with no known postfix is returned unchanged. */
    private static String canonicalName( String physicalName )
    {
        for ( String postfix : POSTFIXES )
        {
            if ( physicalName.endsWith( postfix ) )
            {
                return physicalName.substring( 0, physicalName.length() - postfix.length() );
            }
        }
        return physicalName;
    }

    /** Exposed for the request-shape tests: the physical field names one property expands to. */
    static List<String> expandedFields( String property )
    {
        List<String> fields = new ArrayList<>( POSTFIXES.size() );
        for ( String postfix : POSTFIXES )
        {
            fields.add( property.trim().toLowerCase( Locale.ROOT ) + postfix );
        }
        return fields;
    }
}
