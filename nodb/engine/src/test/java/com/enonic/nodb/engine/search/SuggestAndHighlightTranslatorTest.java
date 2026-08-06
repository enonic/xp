package com.enonic.nodb.engine.search;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Gate D's suggester and highlighter, request and response. JSON→JSON with no OpenSearch in sight,
 * for the same reason {@link QueryDslTranslatorTest} is: the corpus proves the RESULTS, and the
 * traps here are in the request SHAPE — a wrong physical field name in either translator returns
 * nothing at all, silently, which is not a shape a result diff localizes for you.
 */
class SuggestAndHighlightTranslatorTest
{
    // ---- suggesters -----------------------------------------------------------------

    /**
     * The name is the object KEY, {@code text} sits OUTSIDE {@code term}, and the field resolves to
     * the ANALYZED variant — which on this path is {@code ._fulltext} (D1b), not {@code ._analyzed}.
     */
    @Test
    void aTermSuggesterKeysByNameAndTargetsTheFulltextSubField()
    {
        assertEquals( "{\"descSuggest\":{\"text\":\"fsk\",\"term\":{\"field\":\"description._fulltext\"}}}",
                      suggest( "{\"descSuggest\":{\"text\":\"fsk\",\"term\":{\"field\":\"description\"}}}" ) );
    }

    @Test
    void everyParameterXpSetsIsCarriedUnderItsEngineName()
    {
        String body = suggest( "{\"s\":{\"text\":\"laksx\",\"term\":{\"field\":\"description\",\"size\":5,\"analyzer\":\"norwegian\"," +
                                   "\"sort\":\"frequency\",\"suggestMode\":\"always\",\"maxEdits\":2,\"prefixLength\":1," +
                                   "\"minWordLength\":4,\"maxInspections\":5,\"minDocFreq\":0.1,\"maxTermFreq\":0.5}}}" );

        for ( String expected : List.of( "\"size\":5", "\"analyzer\":\"norwegian\"", "\"sort\":\"frequency\"",
                                         "\"suggest_mode\":\"always\"", "\"max_edits\":2", "\"prefix_length\":1", "\"min_word_length\":4",
                                         "\"max_inspections\":5", "\"min_doc_freq\":0.1", "\"max_term_freq\":0.5" ) )
        {
            assertTrue( body.contains( expected ), body + " is missing " + expected );
        }
    }

    /**
     * The compat mapping, and the reason it lives here: {@code jarowinkler} is
     * {@code TermSuggestionQuery.StringDistance.JAROWINKLER}'s public value and appears verbatim in
     * XP's {@code node.ts} type union, so apps pass it as a literal. OpenSearch renamed it.
     */
    @Test
    void jarowinklerIsRenamedToJaroWinklerOnTheEngineSide()
    {
        assertTrue( suggest( "{\"s\":{\"text\":\"x\",\"term\":{\"field\":\"f\",\"stringDistance\":\"jarowinkler\"}}}" ).contains(
            "\"string_distance\":\"jaro_winkler\"" ) );
    }

    @Test
    void everyOtherStringDistanceIsPassedThroughUnchanged()
    {
        for ( String value : List.of( "internal", "damerau_levenshtein", "levenshtein", "ngram" ) )
        {
            assertTrue( suggest( "{\"s\":{\"text\":\"x\",\"term\":{\"field\":\"f\",\"stringDistance\":\"" + value + "\"}}}" ).contains(
                "\"string_distance\":\"" + value + "\"" ) );
        }
    }

    @Test
    void aSuggesterWithoutATermBodyOrAFieldFailsLoudly()
    {
        assertThrows( QueryDslTranslator.UnsupportedQueryException.class, () -> suggest( "{\"s\":{\"text\":\"x\"}}" ) );
        assertThrows( QueryDslTranslator.UnsupportedQueryException.class, () -> suggest( "{\"s\":{\"text\":\"x\",\"term\":{}}}" ) );
        assertThrows( QueryDslTranslator.UnsupportedQueryException.class, () -> suggest( "{\"s\":{\"term\":{\"field\":\"f\"}}}" ) );
    }

    @Test
    void anEmptySuggestConfigProducesNoBlock()
    {
        assertNull( SuggestTranslator.translate( parse( "{}" ) ) );
        assertNull( SuggestTranslator.translate( null ) );
    }

    /**
     * The response shape the client transcribes into {@code Suggestions}: five field names, exactly
     * the ones {@code SuggestionsFactory} reads off the ES objects.
     */
    @Test
    void theSuggestResponseIsReEmittedInTheShapeTheClientDecodes()
    {
        String decoded = SuggestTranslator.decode( parse(
            "{\"suggest\":{\"descSuggest\":[{\"text\":\"laksx\",\"offset\":0,\"length\":5," +
                "\"options\":[{\"text\":\"laks\",\"score\":0.96,\"freq\":2}]}]}}" ) );

        assertEquals( "{\"descSuggest\":[{\"text\":\"laksx\",\"offset\":0,\"length\":5," +
                          "\"options\":[{\"text\":\"laks\",\"score\":0.96,\"freq\":2}]}]}", decoded );
    }

    @Test
    void anEntryWithNoOptionsStillCarriesItsTextOffsetAndLength()
    {
        assertEquals( "{\"s\":[{\"text\":\"fsk\",\"offset\":0,\"length\":3,\"options\":[]}]}", SuggestTranslator.decode(
            parse( "{\"suggest\":{\"s\":[{\"text\":\"fsk\",\"offset\":0,\"length\":3,\"options\":[]}]}}" ) ) );
    }

    @Test
    void aResponseWithNoSuggestSectionDecodesToNothing()
    {
        assertEquals( "", SuggestTranslator.decode( parse( "{\"hits\":{\"hits\":[]}}" ) ) );
        assertEquals( "", SuggestTranslator.decode( null ) );
    }

    // ---- highlighting ---------------------------------------------------------------

    /**
     * The three-field expansion on the nodb path. The Gate 0(c) inventory recorded these as
     * {@code name_analyzed}/{@code name_ngram} — underscore, no dot — and warned that highlighting
     * would silently return nothing if that disagreed with the templates. It does not: XP's
     * separator is a dot and the postfix constants already start with an underscore. What changes
     * here is D1/D1b, so the bare variant is {@code ._text} and the analyzed one {@code ._fulltext}.
     */
    @Test
    void aPropertyExpandsToItsThreePhysicalFields()
    {
        ObjectNodeAssert highlight = highlight( "{\"properties\":[{\"name\":\"title\"}]}" );

        assertEquals( List.of( "title._text", "title._fulltext", "title._ngram" ), highlight.fieldNames() );
        assertEquals( List.of( "title._text", "title._fulltext", "title._ngram" ), HighlightTranslator.expandedFields( "Title" ) );
    }

    @Test
    void theHighlighterTypeIsForcedToPlain()
    {
        assertEquals( "plain", highlight( "{\"properties\":[{\"name\":\"title\"}]}" ).node().path( "type" ).asText() );
    }

    /**
     * OpenSearch flipped this default from {@code false} to {@code true}, and under {@code true} a
     * {@code fulltext()} match on {@code description._fulltext} would stop highlighting the other
     * two variants — dismantling the very expansion above. Sent explicitly on the block AND on
     * every field, defaulting to {@code false} (ES 2.4's default, i.e. the recorded behaviour).
     */
    @Test
    void requireFieldMatchIsAlwaysSentExplicitlyAndDefaultsToFalse()
    {
        ObjectNodeAssert highlight = highlight( "{\"properties\":[{\"name\":\"title\"}]}" );

        assertTrue( highlight.node().has( "require_field_match" ) );
        assertFalse( highlight.node().path( "require_field_match" ).asBoolean() );
        for ( String field : highlight.fieldNames() )
        {
            assertTrue( highlight.field( field ).has( "require_field_match" ), field + " has no explicit require_field_match" );
            assertFalse( highlight.field( field ).path( "require_field_match" ).asBoolean() );
        }
    }

    @Test
    void anExplicitRequireFieldMatchIsHonouredOnBothLevels()
    {
        ObjectNodeAssert highlight = highlight(
            "{\"settings\":{\"requireFieldMatch\":true},\"properties\":[{\"name\":\"t\",\"settings\":{\"requireFieldMatch\":true}}]}" );

        assertTrue( highlight.node().path( "require_field_match" ).asBoolean() );
        assertTrue( highlight.field( "t._fulltext" ).path( "require_field_match" ).asBoolean() );
    }

    @Test
    void theGlobalBlockCarriesXpsSettingsUnderTheirEngineNames()
    {
        JsonNode body = highlight( "{\"settings\":{\"encoder\":\"html\",\"tagsSchema\":\"styled\",\"fragmenter\":\"span\"," +
                                       "\"fragmentSize\":150,\"noMatchSize\":10,\"numOfFragments\":5,\"order\":\"score\"," +
                                       "\"preTags\":[\"<b>\"],\"postTags\":[\"</b>\"]},\"properties\":[{\"name\":\"t\"}]}" ).node();

        assertEquals( "html", body.path( "encoder" ).asText() );
        assertEquals( "styled", body.path( "tags_schema" ).asText() );
        assertEquals( "span", body.path( "fragmenter" ).asText() );
        assertEquals( 150, body.path( "fragment_size" ).asInt() );
        assertEquals( 10, body.path( "no_match_size" ).asInt() );
        assertEquals( 5, body.path( "number_of_fragments" ).asInt() );
        assertEquals( "score", body.path( "order" ).asText() );
        assertEquals( "[\"<b>\"]", body.path( "pre_tags" ).toString() );
        assertEquals( "[\"</b>\"]", body.path( "post_tags" ).toString() );
    }

    /** {@code encoder}/{@code tagsSchema} exist only globally — XP has no per-field form for them. */
    @Test
    void perFieldSettingsCarryNoEncoderOrTagsSchema()
    {
        ObjectNodeAssert highlight = highlight(
            "{\"properties\":[{\"name\":\"t\",\"settings\":{\"numOfFragments\":3,\"encoder\":\"html\",\"tagsSchema\":\"styled\"}}]}" );

        assertEquals( 3, highlight.field( "t._text" ).path( "number_of_fragments" ).asInt() );
        assertFalse( highlight.field( "t._text" ).has( "encoder" ) );
        assertFalse( highlight.field( "t._text" ).has( "tags_schema" ) );
    }

    /**
     * XP's {@code HighlightQuery.empty()} reaches the ES path too and produces a highlight block
     * with no fields, which highlights nothing. Dropping it is the same result, one less thing on
     * the wire — and it is what keeps an ordinary query from carrying a highlight block just
     * because {@code lib-node} always constructs one.
     */
    @Test
    void aHighlightWithNoPropertiesProducesNoBlock()
    {
        assertNull( HighlightTranslator.translate( parse( "{}" ) ) );
        assertNull( HighlightTranslator.translate( parse( "{\"properties\":[]}" ) ) );
    }

    @Test
    void aPropertyWithoutANameFailsLoudly()
    {
        assertThrows( QueryDslTranslator.UnsupportedQueryException.class,
                      () -> HighlightTranslator.translate( parse( "{\"properties\":[{}]}" ) ) );
    }

    /**
     * All three variants strip back to one property name, and the ANALYZED one wins.
     *
     * <p>Merging them is the obvious-looking answer and is wrong: XP normalizes the string variant
     * at index time, so {@code title._text} highlights {@code <em>oslo</em>} while
     * {@code title._fulltext} highlights {@code <em>Oslo</em>} — a merge returns two fragments for a
     * property with one match, which corpus row {@code HIGHLIGHT-01} catches. So one wins, by a
     * defined precedence rather than by the hash order the ES path happened to use.
     */
    @Test
    void theThreeVariantsCollapseOntoOnePropertyAndTheAnalyzedVariantWins()
    {
        assertEquals( Map.of( "title", List.of( "<em>Oslo</em>" ) ), HighlightTranslator.decode( parse(
            "{\"highlight\":{\"title._text\":[\"<em>oslo</em>\"],\"title._fulltext\":[\"<em>Oslo</em>\"]," +
                "\"title._ngram\":[\"<em>Oslo</em>\"]}}" ) ) );

        // …and precedence is by variant, not by response order: the analyzed one still wins last.
        assertEquals( Map.of( "title", List.of( "<em>Oslo</em>" ) ), HighlightTranslator.decode( parse(
            "{\"highlight\":{\"title._ngram\":[\"<em>Osl</em>o\"],\"title._fulltext\":[\"<em>Oslo</em>\"]}}" ) ) );
    }

    /** A variant that matched nothing cannot displace one that did: only present keys compete. */
    @Test
    void aLowerPrecedenceVariantIsUsedWhenTheAnalyzedOneDidNotMatch()
    {
        assertEquals( Map.of( "title", List.of( "<em>oslo</em>" ) ),
                      HighlightTranslator.decode( parse( "{\"highlight\":{\"title._text\":[\"<em>oslo</em>\"]}}" ) ) );
    }

    @Test
    void aFieldWithNoKnownPostfixKeepsItsName()
    {
        assertEquals( Map.of( "odd", List.of( "x" ) ), HighlightTranslator.decode( parse( "{\"highlight\":{\"odd\":[\"x\"]}}" ) ) );
    }

    @Test
    void aHitWithNoHighlightSectionDecodesToNothing()
    {
        assertEquals( Map.of(), HighlightTranslator.decode( parse( "{\"_id\":\"a@draft\"}" ) ) );
    }

    // ---- helpers -------------------------------------------------------------------

    private static String suggest( String canonical )
    {
        return SuggestTranslator.translate( parse( canonical ) ).toString();
    }

    private static ObjectNodeAssert highlight( String canonical )
    {
        return new ObjectNodeAssert( HighlightTranslator.translate( parse( canonical ) ) );
    }

    /** Reads the {@code fields} object of a highlight block without repeating the path everywhere. */
    private record ObjectNodeAssert(JsonNode node)
    {
        List<String> fieldNames()
        {
            return List.copyOf( node.path( "fields" ).properties().stream().map( Map.Entry::getKey ).toList() );
        }

        JsonNode field( String name )
        {
            return node.path( "fields" ).path( name );
        }
    }

    private static JsonNode parse( String json )
    {
        if ( json == null )
        {
            return null;
        }
        try
        {
            return OpenSearchClient.mapper().readTree( json );
        }
        catch ( Exception e )
        {
            throw new IllegalArgumentException( json, e );
        }
    }
}
