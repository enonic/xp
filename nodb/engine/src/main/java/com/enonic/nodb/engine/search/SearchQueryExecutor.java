package com.enonic.nodb.engine.search;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.nodb.engine.TenantContext;

/**
 * Executes a {@link SearchQuery} against OpenSearch: builds the request body, applies the
 * per-source ACL fan-out, runs it through the alias, and decodes the hits.
 *
 * <p><b>The ACL filter is never absent.</b> The Elasticsearch path skipped it entirely when
 * the principal set contained {@code role:system.admin}, and 22 non-test sites construct admin
 * contexts, so that shortcut fired constantly. Here the indexer injects an admin read key into
 * every document instead, which means the filter is always applied and there is no ACL-bypass
 * code path to get wrong. The multi-source form is the {@code IndicesQueryBuilder} replacement
 * the port needs: {@code should[ must[ term _repo, term _branch, terms read-keys ] ]}, one
 * clause per source, so per-source principal sets stay per-source.
 *
 * <p><b>Attribution comes from fields, never from names.</b> Hits carry {@code _repo} and
 * {@code _branch} out of {@code _source}; nothing parses a generational index name back.
 */
public final class SearchQueryExecutor
{
    /** OpenSearch's own default cap on {@code from + size}; deep paging needs a PIT instead. */
    private static final int MAX_RESULT_WINDOW = 10_000;

    private static final int GET_ALL = -1;

    /** Same 60s the Elasticsearch scroll used, extended on every page. */
    private static final java.time.Duration PIT_KEEP_ALIVE = java.time.Duration.ofSeconds( 60 );

    private final OpenSearchClient client;

    public SearchQueryExecutor( OpenSearchClient client )
    {
        this.client = client;
    }

    public record Hit(String id, String repoId, String branch, float score, Map<String, List<Object>> returnValues,
                      List<String> sortValues)
    {
    }

    public record Result(List<Hit> hits, long totalHits, float maxScore)
    {
    }

    public Result execute( TenantContext tenant, SearchQuery query )
    {
        if ( query.sources().isEmpty() )
        {
            throw new QueryDslTranslator.UnsupportedQueryException( "A search request needs at least one source" );
        }

        String target = target( tenant, query );

        if ( query.size() == GET_ALL )
        {
            return executeAll( target, query );
        }
        return decode( client.search( target, body( query, query.from(), query.size() ) ), query );
    }

    /**
     * {@code size = -1} (XP's GET_ALL) — {@code search_after} over a point in time, the
     * replacement for ES 2.4's scroll.
     *
     * <p><b>A total order is mandatory.</b> {@code search_after} resumes from the previous page's
     * sort values, so if two documents can tie on every sort key the walk may skip or repeat them.
     * {@code _shard_doc} is appended as the final tiebreaker: it is unique per document within a
     * PIT and is only available with one, which is the other reason this path needs a PIT rather
     * than plain paging.
     *
     * <p><b>{@code track_total_hits} is always true</b> (in {@link #body}). OpenSearch otherwise
     * stops counting at 10 000 and reports {@code "relation": "gte"}; the corpus compares totals
     * EXACTLY, so an approximate total is a failed row.
     *
     * <p><b>Two quirks of the scroll path are reproduced, not fixed.</b> Elasticsearch read
     * {@code totalHits} and {@code maxScore} from the FINAL, EMPTY page of a scroll, so every
     * GET_ALL query has always reported {@code maxScore = NaN} — callers read it that way, and
     * Gate 0(e) recorded it in the baseline. And {@code explain} was never set on the scroll path,
     * so it is dropped here too ({@link #body} only honours it for a windowed search). Both are
     * deliberate deltas-that-are-not-deltas: matching the baseline is the requirement.
     *
     * <p><b>{@code from} is ignored on this path</b>, as it was on the scroll path — a scroll has no
     * offset, and {@code search_after} has none either. Every GET_ALL caller in XP passes 0.
     */
    private Result executeAll( String target, SearchQuery query )
    {
        int page = query.batchSize() > 0 ? Math.min( query.batchSize(), MAX_RESULT_WINDOW ) : MAX_RESULT_WINDOW;

        String pitId = client.openPit( target, PIT_KEEP_ALIVE );
        try
        {
            List<Hit> hits = new ArrayList<>();
            long totalHits = 0;
            JsonNode searchAfter = null;

            while ( true )
            {
                ObjectNode body = body( query, 0, page );
                // Never on the scroll path, exactly as the Elasticsearch scroll never set it.
                body.remove( "explain" );
                body.set( "pit", OpenSearchClient.mapper()
                    .createObjectNode()
                    .put( "id", pitId )
                    .put( "keep_alive", PIT_KEEP_ALIVE.toSeconds() + "s" ) );
                totalOrder( body );
                if ( searchAfter != null )
                {
                    body.set( "search_after", searchAfter );
                }

                JsonNode response = client.searchPit( body );
                Result batch = decode( response, query );
                totalHits = batch.totalHits();
                batch.hits().forEach( hit -> hits.add( withoutTiebreaker( hit ) ) );

                JsonNode last = lastHit( response );
                if ( last == null || batch.hits().size() < page )
                {
                    break;
                }
                searchAfter = last.path( "sort" );
            }

            return new Result( hits, totalHits, Float.NaN );
        }
        finally
        {
            client.closePit( pitId );
        }
    }

    /**
     * Appends {@code _shard_doc} to the sort array, creating it when the caller gave no sorts —
     * mirroring the scroll path's "adds {@code sort: _doc} only when the caller gave no sorts",
     * except that the tiebreaker is now needed in BOTH cases: a caller-supplied field sort is not
     * a total order either.
     */
    private static void totalOrder( ObjectNode body )
    {
        ArrayNode sort = body.has( "sort" ) ? (ArrayNode) body.get( "sort" ) : body.putArray( "sort" );
        sort.add( "_shard_doc" );
    }

    /**
     * Drops the {@code _shard_doc} value {@link #totalOrder} appended.
     *
     * <p>The tiebreaker is this executor's own paging mechanism, not part of the caller's query: a
     * caller that sorted by one field must see one sort value, exactly as it did on the scroll path.
     * The cursor is taken from the RAW response before this, so stripping it cannot affect the walk.
     */
    private static Hit withoutTiebreaker( Hit hit )
    {
        List<String> values = hit.sortValues();
        if ( values.isEmpty() )
        {
            return hit;
        }
        return new Hit( hit.id(), hit.repoId(), hit.branch(), hit.score(), hit.returnValues(),
                        List.copyOf( values.subList( 0, values.size() - 1 ) ) );
    }

    private static JsonNode lastHit( JsonNode response )
    {
        JsonNode hits = response.path( "hits" ).path( "hits" );
        return hits.isEmpty() ? null : hits.get( hits.size() - 1 );
    }

    private String target( TenantContext tenant, SearchQuery query )
    {
        Set<String> aliases = new LinkedHashSet<>();
        for ( SearchQuery.Source source : query.sources() )
        {
            aliases.add( SearchIndexNames.alias( tenant, source.repoId() ) );
        }
        return String.join( ",", aliases );
    }

    ObjectNode body( SearchQuery query, int from, int size )
    {
        QueryDslTranslator translator = translator( query );
        ObjectNode body = OpenSearchClient.mapper().createObjectNode();

        ObjectNode bool = OpenSearchClient.mapper().createObjectNode();
        bool.set( "must", OpenSearchClient.mapper().createArrayNode().add( translator.translateQuery( parse( query.query() ) ) ) );

        ArrayNode filter = OpenSearchClient.mapper().createArrayNode();
        filter.add( sourceFilter( query ) );
        for ( String queryFilter : query.queryFilters() )
        {
            filter.add( translator.translateFilter( parse( queryFilter ) ) );
        }
        bool.set( "filter", filter );

        body.set( "query", OpenSearchClient.mapper().createObjectNode().set( "bool", bool ) );

        if ( !query.postFilters().isEmpty() )
        {
            body.set( "post_filter", combine( translator, query.postFilters() ) );
        }

        if ( !query.sort().isEmpty() )
        {
            ArrayNode sort = OpenSearchClient.mapper().createArrayNode();
            for ( String element : query.sort() )
            {
                sort.add( translator.translateSort( parse( element ) ) );
            }
            body.set( "sort", sort );
        }

        body.put( "from", Math.max( from, 0 ) );
        body.put( "size", Math.max( size, 0 ) );
        body.put( "track_total_hits", true );
        if ( query.explain() )
        {
            body.put( "explain", true );
        }

        body.set( "_source", sourceIncludes( query ) );

        return body;
    }

    /**
     * The {@code _source} projection always carries {@code _repo}/{@code _branch} — they are
     * the hit's attribution, not an optional return field — plus the physical form of whatever
     * the caller asked for.
     */
    private ObjectNode sourceIncludes( SearchQuery query )
    {
        ArrayNode includes = OpenSearchClient.mapper().createArrayNode();
        includes.add( IndexFields.REPO );
        includes.add( IndexFields.BRANCH );
        for ( String field : query.returnFields() )
        {
            includes.add( IndexFields.physicalName( field ) );
        }
        return OpenSearchClient.mapper().createObjectNode().set( "includes", includes );
    }

    private ObjectNode sourceFilter( SearchQuery query )
    {
        ArrayNode should = OpenSearchClient.mapper().createArrayNode();
        for ( SearchQuery.Source source : query.sources() )
        {
            ArrayNode must = OpenSearchClient.mapper().createArrayNode();
            must.add( term( IndexFields.REPO, source.repoId() ) );
            must.add( term( IndexFields.BRANCH, source.branch() ) );
            must.add( readKeys( source.principals() ) );

            should.add( OpenSearchClient.mapper()
                            .createObjectNode()
                            .set( "bool", OpenSearchClient.mapper().createObjectNode().set( "must", must ) ) );
        }

        ObjectNode bool = OpenSearchClient.mapper().createObjectNode();
        bool.set( "should", should );
        bool.put( "minimum_should_match", 1 );
        return OpenSearchClient.mapper().createObjectNode().set( "bool", bool );
    }

    private ObjectNode readKeys( List<String> principals )
    {
        if ( principals.isEmpty() )
        {
            throw new QueryDslTranslator.UnsupportedQueryException(
                "A source carries no principals; an empty ACL must be fail-closed by the caller, never match-all" );
        }

        ArrayNode terms = OpenSearchClient.mapper().createArrayNode();
        for ( String principal : principals )
        {
            terms.add( QueryDslTranslator.normalize( principal ) );
        }
        return OpenSearchClient.mapper()
            .createObjectNode()
            .set( "terms", OpenSearchClient.mapper()
                .createObjectNode()
                .set( IndexFields.physicalName( IndexFields.PERMISSIONS_READ ), terms ) );
    }

    private ObjectNode term( String field, String value )
    {
        return OpenSearchClient.mapper()
            .createObjectNode()
            .set( "term", OpenSearchClient.mapper().createObjectNode().put( field, value ) );
    }

    /**
     * The translator is per-request, not a static utility: resolving an {@code _id} predicate needs
     * the request's source branches, because the document id is the composite
     * {@code <nodeId>@<branch>} (D10) and the bare node id is not a field.
     */
    private static QueryDslTranslator translator( SearchQuery query )
    {
        List<String> branches = new ArrayList<>();
        for ( SearchQuery.Source source : query.sources() )
        {
            if ( !branches.contains( source.branch() ) )
            {
                branches.add( source.branch() );
            }
        }
        return new QueryDslTranslator( branches );
    }

    private JsonNode combine( QueryDslTranslator translator, List<String> filters )
    {
        if ( filters.size() == 1 )
        {
            return translator.translateFilter( parse( filters.get( 0 ) ) );
        }
        ArrayNode must = OpenSearchClient.mapper().createArrayNode();
        for ( String filter : filters )
        {
            must.add( translator.translateFilter( parse( filter ) ) );
        }
        return OpenSearchClient.mapper().createObjectNode().set( "bool", OpenSearchClient.mapper().createObjectNode().set( "must", must ) );
    }

    private Result decode( JsonNode response, SearchQuery query )
    {
        JsonNode hitsNode = response.path( "hits" );

        long totalHits = hitsNode.path( "total" ).path( "value" ).asLong();
        JsonNode maxScoreNode = hitsNode.get( "max_score" );
        // NaN when the results are sorted by field, exactly as the Elasticsearch path reported
        // it — reproduced rather than "fixed", because callers already read it that way.
        float maxScore = maxScoreNode == null || maxScoreNode.isNull() ? Float.NaN : (float) maxScoreNode.asDouble();

        List<Hit> hits = new ArrayList<>();
        for ( JsonNode hit : hitsNode.path( "hits" ) )
        {
            hits.add( decodeHit( hit, query ) );
        }

        return new Result( hits, totalHits, maxScore );
    }

    private Hit decodeHit( JsonNode hit, SearchQuery query )
    {
        JsonNode source = hit.path( "_source" );

        Map<String, List<Object>> returnValues = new LinkedHashMap<>();
        for ( String field : query.returnFields() )
        {
            List<Object> values = extract( source, IndexFields.physicalName( field ) );
            if ( !values.isEmpty() )
            {
                returnValues.put( field, values );
            }
        }

        List<String> sortValues = new ArrayList<>();
        for ( JsonNode sortValue : hit.path( "sort" ) )
        {
            sortValues.add( sortValue.asText() );
        }

        JsonNode score = hit.get( "_score" );

        return new Hit( nodeId( hit ), single( extract( source, IndexFields.REPO ) ), single( extract( source, IndexFields.BRANCH ) ),
                        score == null || score.isNull() ? Float.NaN : (float) score.asDouble(), returnValues, sortValues );
    }

    /**
     * The document {@code _id} is the composite {@code <nodeId>@<branch>} (D10) and callers
     * want the bare node id. It is stripped here rather than parsed anywhere else — the
     * separator cannot occur in either component, so the split is exact.
     */
    private static String nodeId( JsonNode hit )
    {
        String id = hit.path( "_id" ).asText();
        int at = id.lastIndexOf( '@' );
        return at < 0 ? id : id.substring( 0, at );
    }

    private static String single( List<Object> values )
    {
        return values.isEmpty() ? null : String.valueOf( values.get( 0 ) );
    }

    /** Walks a dotted physical name through the expanded {@code _source} object tree. */
    private static List<Object> extract( JsonNode source, String physicalName )
    {
        JsonNode node = source;
        for ( String segment : physicalName.split( "\\." ) )
        {
            node = node.get( segment );
            if ( node == null )
            {
                return List.of();
            }
        }

        List<Object> values = new ArrayList<>();
        for ( JsonNode value : node.isArray() ? node : OpenSearchClient.mapper().createArrayNode().add( node ) )
        {
            values.add( scalar( value ) );
        }
        return values;
    }

    private static Object scalar( JsonNode value )
    {
        if ( value.isNumber() )
        {
            return value.numberValue();
        }
        if ( value.isBoolean() )
        {
            return value.booleanValue();
        }
        return value.asText();
    }

    private static JsonNode parse( String json )
    {
        try
        {
            return OpenSearchClient.mapper().readTree( json );
        }
        catch ( Exception e )
        {
            throw new QueryDslTranslator.UnsupportedQueryException( "Malformed query DSL document: " + e.getMessage() );
        }
    }
}
