package com.enonic.xp.repo.impl.search.dsl;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.highlight.HighlightPropertySettings;
import com.enonic.xp.query.highlight.HighlightQuery;
import com.enonic.xp.query.highlight.HighlightQueryProperty;
import com.enonic.xp.query.highlight.HighlightQuerySettings;
import com.enonic.xp.query.highlight.constants.Encoder;
import com.enonic.xp.query.highlight.constants.Fragmenter;
import com.enonic.xp.query.highlight.constants.Order;
import com.enonic.xp.query.highlight.constants.TagsSchema;
import com.enonic.xp.query.parser.QueryParser;
import com.enonic.xp.query.suggester.TermSuggestionQuery;
import com.enonic.xp.storage.spi.SearchDsl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The suggester and highlight halves of the wire envelope (Gate D). Both slots were rejected
 * outright until this gate, so these are the rows that prove the rejection became a rendering and
 * not a silent drop — and that the wire carries XP's OWN vocabulary, since the backend is what owns
 * the engine's spelling and the physical field layout.
 */
class SuggestAndHighlightDslRendererTest
{
    @Test
    void aTermSuggesterRendersWithTextOutsideTheTermBody()
    {
        final SearchDsl dsl = render( NodeQuery.create()
                                          .query( QueryParser.parse( "_path LIKE '/x*'" ) )
                                          .addSuggestionQuery(
                                              TermSuggestionQuery.create( "descSuggest" ).field( "myField" ).text( "fsk" ).build() )
                                          .build() );

        // suggestMode rides along because it is the one parameter with a non-null builder default
        // (MISSING) -- see onlySuggestModeIsCarriedByDefault. The field name is post-IndexPath
        // (lower-cased) and carries NO sub-field postfix: resolving it is the backend's job.
        assertEquals( Map.of( "descSuggest", Map.of( "text", "fsk", "term", Map.of( "field", "myfield", "suggestMode", "missing" ) ) ),
                      dsl.getSuggest() );
    }

    /**
     * Every parameter, in XP's own spelling and with XP's own values — {@code suggestMode} not
     * {@code suggest_mode}, and {@code jarowinkler} not {@code jaro_winkler}. The rename is the
     * backend's business precisely because {@code jarowinkler} is XP's public API value.
     */
    @Test
    void everyTermSuggesterParameterIsCarriedInXpsVocabulary()
    {
        final SearchDsl dsl = render( NodeQuery.create()
                                          .query( QueryParser.parse( "_path LIKE '/x*'" ) )
                                          .addSuggestionQuery( TermSuggestionQuery.create( "s" )
                                                                   .field( "f" )
                                                                   .text( "t" )
                                                                   .size( 5 )
                                                                   .analyzer( "norwegian" )
                                                                   .sort( TermSuggestionQuery.Sort.FREQUENCY )
                                                                   .suggestMode( TermSuggestionQuery.SuggestMode.ALWAYS )
                                                                   .maxEdits( 2 )
                                                                   .prefixLength( 1 )
                                                                   .minWordLength( 4 )
                                                                   .maxInspections( 5 )
                                                                   .minDocFreq( 0.1f )
                                                                   .maxTermFreq( 0.5f )
                                                                   .stringDistance( TermSuggestionQuery.StringDistance.JAROWINKLER )
                                                                   .build() )
                                          .build() );

        @SuppressWarnings("unchecked") final Map<String, Object> term =
            (Map<String, Object>) ( (Map<String, Object>) dsl.getSuggest().get( "s" ) ).get( "term" );

        assertEquals( "f", term.get( "field" ) );
        assertEquals( 5, term.get( "size" ) );
        assertEquals( "norwegian", term.get( "analyzer" ) );
        assertEquals( "frequency", term.get( "sort" ) );
        assertEquals( "always", term.get( "suggestMode" ) );
        assertEquals( 2, term.get( "maxEdits" ) );
        assertEquals( 1, term.get( "prefixLength" ) );
        assertEquals( 4, term.get( "minWordLength" ) );
        assertEquals( 5, term.get( "maxInspections" ) );
        assertEquals( 0.1f, term.get( "minDocFreq" ) );
        assertEquals( 0.5f, term.get( "maxTermFreq" ) );
        assertEquals( "jarowinkler", term.get( "stringDistance" ) );
    }

    /**
     * {@code suggestMode} is the one parameter with a non-null builder default, so it is always on
     * the wire; the others are absent unless set, because an unset suggester parameter is the
     * engine's own default and this renderer has no opinion about it.
     */
    @Test
    void onlySuggestModeIsCarriedByDefault()
    {
        final SearchDsl dsl = render( NodeQuery.create()
                                          .query( QueryParser.parse( "_path LIKE '/x*'" ) )
                                          .addSuggestionQuery( TermSuggestionQuery.create( "s" ).field( "f" ).text( "t" ).build() )
                                          .build() );

        @SuppressWarnings("unchecked") final Map<String, Object> term =
            (Map<String, Object>) ( (Map<String, Object>) dsl.getSuggest().get( "s" ) ).get( "term" );

        assertEquals( List.of( "field", "suggestMode" ), List.copyOf( term.keySet() ) );
    }

    /** Suggester order is part of the format: a set's iteration order is not stable across JVMs. */
    @Test
    void suggestersAreOrderedByName()
    {
        final SearchDsl dsl = render( NodeQuery.create()
                                          .query( QueryParser.parse( "_path LIKE '/x*'" ) )
                                          .addSuggestionQuery( TermSuggestionQuery.create( "zebra" ).field( "f" ).text( "t" ).build() )
                                          .addSuggestionQuery( TermSuggestionQuery.create( "alpha" ).field( "f" ).text( "t" ).build() )
                                          .build() );

        assertEquals( List.of( "alpha", "zebra" ), List.copyOf( dsl.getSuggest().keySet() ) );
    }

    @Test
    void aHighlightCarriesItsPropertiesWithoutSubFieldPostfixes()
    {
        final SearchDsl dsl = render( NodeQuery.create()
                                          .query( QueryParser.parse( "_path LIKE '/x*'" ) )
                                          .highlight( HighlightQuery.create()
                                                          .property( HighlightQueryProperty.create( "MyTitle" )
                                                                         .settings( HighlightPropertySettings.create()
                                                                                        .numOfFragments( 3 )
                                                                                        .build() )
                                                                         .build() )
                                                          .build() )
                                          .build() );

        // The three-field expansion belongs to the backend: the postfixes are not even spelled the
        // same way on the nodb path (._text/._fulltext/._ngram) as on the ES path.
        // requireFieldMatch appears on both levels because XP's settings builder defaults it to
        // false rather than null -- which the ES path also sends, so it is not this gate's doing.
        assertEquals( Map.of( "settings", Map.of( "requireFieldMatch", false ), "properties",
                              List.of( Map.of( "name", "mytitle", "settings", Map.of( "numOfFragments", 3, "requireFieldMatch", false ) ) ) ),
                      dsl.getHighlight() );
    }

    @Test
    void theGlobalBlockCarriesEncoderAndTagsSchemaWhichPerPropertySettingsCannot()
    {
        final SearchDsl dsl = render( NodeQuery.create()
                                          .query( QueryParser.parse( "_path LIKE '/x*'" ) )
                                          .highlight( HighlightQuery.create()
                                                          .settings( HighlightQuerySettings.create()
                                                                         .encoder( Encoder.HTML )
                                                                         .tagsSchema( TagsSchema.STYLED )
                                                                         .fragmenter( Fragmenter.SPAN )
                                                                         .fragmentSize( 150 )
                                                                         .noMatchSize( 10 )
                                                                         .numOfFragments( 5 )
                                                                         .order( Order.SCORE )
                                                                         .addPreTags( List.of( "<b>" ) )
                                                                         .addPostTags( List.of( "</b>" ) )
                                                                         .build() )
                                                          .property( HighlightQueryProperty.create( "t" ).build() )
                                                          .build() )
                                          .build() );

        @SuppressWarnings("unchecked") final Map<String, Object> settings = (Map<String, Object>) dsl.getHighlight().get( "settings" );

        assertEquals( "html", settings.get( "encoder" ) );
        assertEquals( "styled", settings.get( "tagsSchema" ) );
        assertEquals( "span", settings.get( "fragmenter" ) );
        assertEquals( 150, settings.get( "fragmentSize" ) );
        assertEquals( 10, settings.get( "noMatchSize" ) );
        assertEquals( 5, settings.get( "numOfFragments" ) );
        assertEquals( "score", settings.get( "order" ) );
        assertEquals( List.of( "<b>" ), settings.get( "preTags" ) );
        assertEquals( List.of( "</b>" ), settings.get( "postTags" ) );
    }

    /**
     * {@code lib-node} builds a {@code HighlightQuery.empty()} for a query with no highlight block
     * at all, so an empty one must mean "no highlighting" rather than "highlight with defaults" —
     * otherwise every scripted query would carry a highlight block. Until this gate the mere
     * presence of a non-null {@code HighlightQuery} was a hard rejection, which is the same trap
     * from the other side.
     */
    @Test
    void anEmptyHighlightIsNotOnTheWireAtAll()
    {
        final SearchDsl dsl = render(
            NodeQuery.create().query( QueryParser.parse( "_path LIKE '/x*'" ) ).highlight( HighlightQuery.empty() ).build() );

        assertTrue( dsl.getHighlight().isEmpty() );
    }

    @Test
    void aQueryWithNeitherCarriesNeither()
    {
        final SearchDsl dsl = render( NodeQuery.create().query( QueryParser.parse( "_path LIKE '/x*'" ) ).build() );

        assertTrue( dsl.getSuggest().isEmpty() );
        assertTrue( dsl.getHighlight().isEmpty() );
        assertFalse( dsl.getQuery().isEmpty() );
    }

    /** Aggregations are the remaining fence and must still fail loudly rather than be dropped. */
    @Test
    void aggregationsStillHaveNoWireForm()
    {
        final NodeQuery query = NodeQuery.create()
            .query( QueryParser.parse( "_path LIKE '/x*'" ) )
            .addAggregationQuery( com.enonic.xp.query.aggregation.TermsAggregationQuery.create( "byType" ).fieldName( "_type" ).build() )
            .build();

        assertThrows( DslRenderException.class, () -> render( query ) );
    }

    private static SearchDsl render( final NodeQuery query )
    {
        return SearchDslRenderer.render( query );
    }
}
