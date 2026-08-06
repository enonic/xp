package com.enonic.nodb.engine.search;

import java.util.Locale;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Suggesters, both directions: XP's canonical suggest config → an OpenSearch {@code suggest} block,
 * and the response's {@code suggest} section → the shape {@code SuggestionsFactory} expects.
 *
 * <h2>The suggester surface is exactly one type and fourteen parameters</h2>
 * XP has a single suggester — {@code TermSuggestionQuery}. There is no completion, phrase or
 * context suggester anywhere in the codebase, and {@code SuggestionQueryBuilderFactory} throws on
 * anything else, so this translator does too rather than inventing coverage. The parameters are
 * {@code name}/{@code field}/{@code text} (all required), {@code analyzer} and {@code size} from
 * the shared base, plus {@code sort}, {@code suggestMode}, {@code maxEdits}, {@code prefixLength},
 * {@code minWordLength}, {@code maxInspections}, {@code minDocFreq}, {@code maxTermFreq} and
 * {@code stringDistance}. XP passes every one of them through when non-null and NONE of the term
 * suggester's other engine parameters ({@code accuracy}, {@code shard_size}) — which is why they
 * are absent here: an unset parameter is the engine default on both sides.
 *
 * <h2>The ctor inversion</h2>
 * ES 2.4 took the suggester NAME in the builder constructor and the field as a setter; OpenSearch
 * inverts it (field in the constructor, name at {@code addSuggestion}). On a JSON wire that
 * distinction disappears entirely — the name is the object KEY under {@code suggest} and
 * {@code field} is a member of the {@code term} object — which is one more reason this port emits
 * JSON instead of driving a typed client. The inversion is recorded because it is what a builder-
 * based port would have had to get right.
 *
 * <h2>{@code jarowinkler} → {@code jaro_winkler}</h2>
 * OpenSearch renamed the string-distance algorithm. The old spelling is not a detail of the ES
 * client: it is {@code TermSuggestionQuery.StringDistance.JAROWINKLER}'s {@code value()}, it is in
 * XP's public {@code node.ts} type union, and apps pass it as a literal string. So the wire keeps
 * XP's vocabulary and the RENAME HAPPENS HERE, in the same place and for the same reason
 * {@code _analyzed} becomes {@code _fulltext}: the server owns the engine's spelling. Changing the
 * XP enum would be a public API break for a private engine detail.
 */
final class SuggestTranslator
{
    /** XP's value → OpenSearch's. Only one entry today; a map because the reason generalizes. */
    private static final Map<String, String> STRING_DISTANCE = Map.of( "jarowinkler", "jaro_winkler" );

    /** Canonical (XP) parameter name → OpenSearch parameter name, all inside the {@code term} object. */
    private static final Map<String, String> TERM_PARAMS = Map.ofEntries( Map.entry( "size", "size" ),
                                                                         Map.entry( "analyzer", "analyzer" ),
                                                                         Map.entry( "sort", "sort" ),
                                                                         Map.entry( "suggestMode", "suggest_mode" ),
                                                                         Map.entry( "maxEdits", "max_edits" ),
                                                                         Map.entry( "prefixLength", "prefix_length" ),
                                                                         Map.entry( "minWordLength", "min_word_length" ),
                                                                         Map.entry( "maxInspections", "max_inspections" ),
                                                                         Map.entry( "minDocFreq", "min_doc_freq" ),
                                                                         Map.entry( "maxTermFreq", "max_term_freq" ) );

    private SuggestTranslator()
    {
    }

    /**
     * @param suggest canonical config: {@code {"<name>": {"text": .., "term": {"field": .., ..}}}}
     * @return the {@code suggest} block, or {@code null} when nothing was requested
     */
    static ObjectNode translate( JsonNode suggest )
    {
        if ( suggest == null || !suggest.isObject() || suggest.isEmpty() )
        {
            return null;
        }

        ObjectNode body = OpenSearchClient.mapper().createObjectNode();
        suggest.properties().forEach( entry -> body.set( entry.getKey(), suggester( entry.getKey(), entry.getValue() ) ) );
        return body;
    }

    private static ObjectNode suggester( String name, JsonNode config )
    {
        JsonNode term = config.get( "term" );
        if ( term == null || !term.isObject() )
        {
            throw new QueryDslTranslator.UnsupportedQueryException(
                "Suggester '" + name + "' has no 'term' body; the term suggester is the only type XP defines" );
        }

        String text = config.path( "text" ).asText( null );
        if ( text == null )
        {
            throw new QueryDslTranslator.UnsupportedQueryException( "Suggester '" + name + "' needs a 'text'" );
        }
        String field = term.path( "field" ).asText( null );
        if ( field == null || field.isBlank() )
        {
            throw new QueryDslTranslator.UnsupportedQueryException( "Suggester '" + name + "' needs a 'field'" );
        }

        ObjectNode termBody = OpenSearchClient.mapper().createObjectNode();
        // The analyzed variant, exactly as TermSuggestionQueryBuilderFactory resolves it
        // (StaticIndexValueType.ANALYZED) -- so ._fulltext on this path (D1b).
        termBody.put( "field", IndexFields.physicalName( field.trim().toLowerCase( Locale.ROOT ) + "._analyzed" ) );

        for ( Map.Entry<String, String> param : TERM_PARAMS.entrySet() )
        {
            JsonNode value = term.get( param.getKey() );
            if ( value != null && !value.isNull() )
            {
                termBody.set( param.getValue(), value );
            }
        }

        JsonNode stringDistance = term.get( "stringDistance" );
        if ( stringDistance != null && !stringDistance.isNull() )
        {
            String value = stringDistance.asText();
            termBody.put( "string_distance", STRING_DISTANCE.getOrDefault( value, value ) );
        }

        ObjectNode suggester = OpenSearchClient.mapper().createObjectNode();
        suggester.put( "text", text );
        suggester.set( "term", termBody );
        return suggester;
    }

    /**
     * The response's {@code suggest} section, re-emitted in the canonical shape the XP client
     * decodes into {@code Suggestions}:
     * {@code {"<name>": [{text, offset, length, options: [{text, score, freq}]}]}}.
     *
     * <p>That is the engine's own response shape, and deliberately so — {@code SuggestionsFactory}
     * reads exactly these fields off the ES objects ({@code entry.getText()},
     * {@code entry.getOffset()}, {@code entry.getLength()}, {@code option.getText()},
     * {@code option.getScore()}, and the term-only {@code option.getFreq()}), so passing the same
     * five names through means the client's decoder is a transcription rather than a mapping.
     * Fields are copied explicitly rather than forwarded wholesale: an engine that later adds a
     * key must not silently widen XP's contract.
     */
    static String decode( JsonNode response )
    {
        JsonNode suggest = response == null ? null : response.get( "suggest" );
        if ( suggest == null || !suggest.isObject() || suggest.isEmpty() )
        {
            return "";
        }

        ObjectNode result = OpenSearchClient.mapper().createObjectNode();
        suggest.properties().forEach( entry -> {
            ArrayNode entries = OpenSearchClient.mapper().createArrayNode();
            for ( JsonNode raw : entry.getValue() )
            {
                entries.add( decodeEntry( raw ) );
            }
            result.set( entry.getKey(), entries );
        } );

        return result.toString();
    }

    private static ObjectNode decodeEntry( JsonNode raw )
    {
        ObjectNode entry = OpenSearchClient.mapper().createObjectNode();
        entry.put( "text", raw.path( "text" ).asText( "" ) );
        entry.put( "offset", raw.path( "offset" ).asInt() );
        entry.put( "length", raw.path( "length" ).asInt() );

        ArrayNode options = OpenSearchClient.mapper().createArrayNode();
        for ( JsonNode raw2 : raw.path( "options" ) )
        {
            ObjectNode option = OpenSearchClient.mapper().createObjectNode();
            option.put( "text", raw2.path( "text" ).asText( "" ) );
            option.put( "score", (float) raw2.path( "score" ).asDouble() );
            if ( raw2.has( "freq" ) )
            {
                option.put( "freq", raw2.path( "freq" ).asInt() );
            }
            options.add( option );
        }
        entry.set( "options", options );
        return entry;
    }
}
