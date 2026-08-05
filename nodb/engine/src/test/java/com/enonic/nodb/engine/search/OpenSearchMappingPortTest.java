package com.enonic.nodb.engine.search;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The mapping port against a REAL OpenSearch 3.7.0: the template is accepted, and — the part that
 * matters — the dynamic templates actually FIRE.
 *
 * <p><b>Why this test exists at all.</b> Blocker 2's failure mode is that {@code match: "*._analyzed"}
 * never matches after dot expansion, and index creation succeeds, indexing succeeds, and queries
 * return zero hits with no error anywhere. Nothing short of indexing a document and running a query
 * can distinguish a working mapping from that. So this class does exactly that, per family, and the
 * mapping-shape assertions are the second witness: if a template had not fired, the field would
 * have been mapped by OpenSearch's dynamic default ({@code text} with a {@code keyword} sub-field)
 * instead of by the template's own type.
 *
 * <p>Runs on the STOCK image — no analysis-icu — which is itself an assertion: under D8 nothing in
 * the settings needs the plugin any more.
 */
class OpenSearchMappingPortTest
{
    private static final String INDEX = "porttest-repo+g1";

    private static final String ALIAS = "porttest-repo";

    private static OpenSearchClient client;

    @BeforeAll
    static void createTheIndexFromTheRealTemplate()
    {
        client = SearchTestFixture.openSearchClient();
        if ( client.indexExists( INDEX ) )
        {
            client.deleteIndex( INDEX );
        }
        // If any ES-2.4-ism survived the port, THIS call fails -- mapper_parsing_exception,
        // unknown parameter, "No handler for type [string]", "The [standard] token filter has been
        // removed", and so on. The whole 13-item list is verified by this one line succeeding.
        client.createIndex( INDEX, IndexTemplate.load().createIndexBody( ALIAS, client.config() ) );
    }

    /**
     * One XP-shaped document, built through the real projection: bare + sub-fields for the same
     * property, a path field, a number, a date, an ngram, a collated order-by, an ACL, and the
     * identity fields. This is the document ES 2.4 accepted only because of
     * {@code -Dmapper.allow_dots_in_name=true}.
     */
    private static void indexTheCorpusDocument()
    {
        Map<String, List<SearchDocument.Value>> fields = new LinkedHashMap<>();
        text( fields, "data.title", "Hello Brave World" );
        text( fields, "data.title._analyzed", "Hello Brave World" );
        text( fields, "data.title._ngram", "Hello Brave World" );
        text( fields, "data.title._orderby", "hello brave world" );
        text( fields, "data.title._orderby_no", "ærlig" );
        text( fields, "data.title._stemmed_en", "running quickly" );
        text( fields, "_path", "/content/mysite/page" );
        text( fields, "_path._path", "/content/mysite/page" );
        text( fields, "_name", "page" );
        text( fields, "_allText._analyzed", "Hello Brave World page" );
        text( fields, IndexFields.PERMISSIONS_READ, "role:system.everyone" );
        fields.put( "data.count._number", List.of( new SearchDocument.Value.Number( 42 ) ) );
        fields.put( "_ts._datetime", List.of( new SearchDocument.Value.Timestamp( 1_700_000_000_000L ) ) );

        SearchDocument document = new SearchDocument( "node-1", null, fields );
        BulkRequest bulk = new BulkRequest();
        bulk.index( INDEX, IndexDocumentProjection.documentId( document, "master" ),
                    IndexDocumentProjection.project( document, "repo", "master" ) );
        client.bulk( bulk.toNdjson() );
        client.refresh( List.of( INDEX ) );
    }

    private static void text( Map<String, List<SearchDocument.Value>> fields, String name, String value )
    {
        fields.put( name, List.of( new SearchDocument.Value.Text( value ) ) );
    }

    // ------------------------------------------------------------------------------ blocker 1

    /**
     * BLOCKER 1: with the {@code _text} postfix, the bare variant and its sub-fields coexist. Before
     * the fix this indexing call fails with {@code can't merge a non object mapping [data.title]
     * with an object mapping} on the FIRST document of every repo.
     */
    @Test
    void theBareVariantAndItsSubFieldsCoexistInOneDocument()
    {
        indexTheCorpusDocument();
        assertEquals( 1, client.count( INDEX ) );
    }

    // ------------------------------------------------------------------------------ blocker 2

    /**
     * BLOCKER 2, proved by query rather than by inspection. Each of these returns zero hits — with
     * no error — if its dynamic template did not fire.
     */
    @Test
    void everyDynamicTemplateFiresProvenByQueryingAnIndexedDocument()
    {
        indexTheCorpusDocument();

        // _text: keyword + lowercase normalizer, NOT tokenized. Gate 0(e) measured all four of
        // these behaviours on ES 2.4's bare field, so they are parity assertions, not preferences.
        assertEquals( 1, hits( term( "data.title._text", "hello brave world" ) ), "exact value, lowercased" );
        assertEquals( 1, hits( term( "data.title._text", "HELLO BRAVE WORLD" ) ), "normalizer lowercases the query term too" );
        assertEquals( 0, hits( term( "data.title._text", "hello" ) ), "a single token must NOT match: _text is not tokenized" );
        assertEquals( 0, hits( term( "data.title._text", "hello brave wörld" ) ), "no asciifolding on _text" );

        // _fulltext: analyzed, tokenized, and accent-insensitive because asciifolding is in the
        // chain. That asymmetry against _text is behaviour, not a bug (corpus UNTYPED-05).
        assertEquals( 1, hits( match( "data.title._fulltext", "brave" ) ), "tokenized" );
        assertEquals( 1, hits( match( "data.title._fulltext", "wörld" ) ), "asciifolding" );

        // _ngram: edge_ngram at index time, so a prefix matches.
        assertEquals( 1, hits( match( "data.title._ngram", "brav" ) ) );

        // _path: path_hierarchy tokenizer, so an ancestor path matches.
        assertEquals( 1, hits( match( "_path._path", "/content/mysite" ) ) );
        assertEquals( 1, hits( term( "_path._text", "/content/mysite/page" ) ) );

        // _stemmed_en: the english analyzer stems, so the stem matches.
        assertEquals( 1, hits( match( "data.title._stemmed_en", "run" ) ) );

        // _number / _datetime: real double and date types, so RANGE works (a keyword would not).
        assertEquals( 1, hits( range( "data.count._number", 41, 43 ) ) );
        assertEquals( 1, hits( range( "_ts._datetime", 1_600_000_000_000L, 1_800_000_000_000L ) ) );

        // The injected admin read key, which admin queries depend on existing (DESIGN §7.2).
        assertEquals( 1, hits( term( "_permissions.read._text", IndexFields.ADMIN_PRINCIPAL ) ) );
        assertEquals( 1, hits( term( "_permissions.read._text", "role:system.everyone" ) ) );

        // The identity fields, with branch case preserved.
        assertEquals( 1, hits( term( IndexFields.BRANCH, "master" ) ) );
        assertEquals( 1, hits( term( IndexFields.REPO, "repo" ) ) );
    }

    /**
     * The second witness: the LIVE mapping shows each template's own type. A template that never
     * fired would leave the field on OpenSearch's dynamic default ({@code text} + a
     * {@code keyword} sub-field), which this distinguishes.
     */
    @Test
    void theLiveMappingShowsTheTemplatesTypesNotTheDynamicDefault()
    {
        indexTheCorpusDocument();
        JsonNode properties = client.getMapping( INDEX ).path( INDEX ).path( "mappings" ).path( "properties" );

        JsonNode title = properties.path( "data" ).path( "properties" ).path( "title" ).path( "properties" );
        assertEquals( "keyword", title.path( "_text" ).path( "type" ).asText() );
        assertEquals( "keywordlowercase", title.path( "_text" ).path( "normalizer" ).asText() );
        assertEquals( "text", title.path( "_fulltext" ).path( "type" ).asText() );
        assertEquals( "document_index_default", title.path( "_fulltext" ).path( "analyzer" ).asText() );
        assertEquals( "text", title.path( "_ngram" ).path( "type" ).asText() );
        assertEquals( "ngram_index_front", title.path( "_ngram" ).path( "analyzer" ).asText() );
        assertEquals( "keyword", title.path( "_orderby" ).path( "type" ).asText() );
        assertEquals( "keyword", title.path( "_orderby_no" ).path( "type" ).asText() );
        assertEquals( "double",
                      properties.path( "data" ).path( "properties" ).path( "count" ).path( "properties" ).path( "_number" ).path( "type" )
                          .asText() );
        assertEquals( "date", properties.path( "_ts" ).path( "properties" ).path( "_datetime" ).path( "type" ).asText() );
        assertEquals( "text", properties.path( "_path" ).path( "properties" ).path( "_path" ).path( "type" ).asText() );
        assertEquals( "keyword", properties.path( IndexFields.BRANCH ).path( "type" ).asText() );

        // The dynamic default would have produced a `fields.keyword` sub-field. None of these have one.
        assertTrue( title.path( "_text" ).path( "fields" ).isMissingNode(), "a keyword template fired, not the dynamic default" );
    }

    /**
     * The end of the D8 chain: XP-side hex collation keys, stored as a plain keyword, produce the
     * Norwegian order {@code a z æ ø å} when OpenSearch sorts on them. Gate 0(d) proved the same
     * ordering through {@code icu_collation_keyword}; this proves the plugin is not needed to get it.
     */
    @Test
    void collationKeysSortPerLocaleThroughARealOpenSearchSort()
    {
        String sortIndex = "porttest-sort+g1";
        if ( client.indexExists( sortIndex ) )
        {
            client.deleteIndex( sortIndex );
        }
        client.createIndex( sortIndex, IndexTemplate.load().createIndexBody( "porttest-sort", client.config() ) );

        List<String> values = List.of( "å", "æ", "z", "ø", "a" );
        BulkRequest bulk = new BulkRequest();
        for ( String value : values )
        {
            Map<String, List<SearchDocument.Value>> fields = new LinkedHashMap<>();
            text( fields, "data.title", value );
            text( fields, "data.title._orderby_no", value );
            text( fields, "data.title._orderby_sv", value );
            text( fields, "data.title._orderby", value );
            SearchDocument document = new SearchDocument( "n-" + value, null, fields );
            bulk.index( sortIndex, IndexDocumentProjection.documentId( document, "master" ),
                        IndexDocumentProjection.project( document, "repo", "master" ) );
        }
        client.bulk( bulk.toNdjson() );
        client.refresh( List.of( sortIndex ) );

        assertEquals( List.of( "a", "z", "æ", "ø", "å" ), sortedBy( sortIndex, "data.title._orderby_no" ) );
        // Swedish disagrees with Norwegian on these letters -- the reason the locale is in the name.
        assertNotEquals( sortedBy( sortIndex, "data.title._orderby_no" ), sortedBy( sortIndex, "data.title._orderby_sv" ) );
        // And the plain _orderby field, which is a codepoint sort, gets Norwegian wrong.
        assertEquals( List.of( "a", "z", "å", "æ", "ø" ), sortedBy( sortIndex, "data.title._orderby" ) );

        client.deleteIndex( sortIndex );
    }

    // --------------------------------------------------------------------------------- helpers

    private static List<String> sortedBy( String index, String field )
    {
        ObjectNode body = OpenSearchClient.mapper().createObjectNode();
        body.put( "size", 50 );
        body.putArray( "sort" ).addObject().putObject( field ).put( "order", "asc" );
        JsonNode response = client.search( index, body );

        List<String> ordered = new ArrayList<>();
        for ( JsonNode hit : response.path( "hits" ).path( "hits" ) )
        {
            ordered.add( hit.path( "_source" ).path( "data" ).path( "title" ).path( "_text" ).asText() );
        }
        return ordered;
    }

    private static long hits( ObjectNode query )
    {
        ObjectNode body = OpenSearchClient.mapper().createObjectNode();
        body.set( "query", query );
        body.put( "track_total_hits", true );
        return client.search( INDEX, body ).path( "hits" ).path( "total" ).path( "value" ).asLong();
    }

    private static ObjectNode term( String field, String value )
    {
        ObjectNode query = OpenSearchClient.mapper().createObjectNode();
        query.putObject( "term" ).put( field, value );
        return query;
    }

    private static ObjectNode match( String field, String value )
    {
        ObjectNode query = OpenSearchClient.mapper().createObjectNode();
        query.putObject( "match" ).put( field, value );
        return query;
    }

    private static ObjectNode range( String field, long from, long to )
    {
        ObjectNode query = OpenSearchClient.mapper().createObjectNode();
        query.putObject( "range" ).putObject( field ).put( "gte", from ).put( "lte", to );
        return query;
    }
}
