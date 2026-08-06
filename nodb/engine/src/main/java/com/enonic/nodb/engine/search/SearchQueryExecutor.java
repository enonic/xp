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
     * {@code size = -1} (XP's GET_ALL) is paged with {@code from}/{@code size} up to
     * OpenSearch's result window. Beyond it this throws rather than silently truncating: real
     * deep paging is {@code search_after} over a point-in-time with a total order appended,
     * which belongs with the structured-query batch that also owns cursor semantics.
     */
    private Result executeAll( String target, SearchQuery query )
    {
        int page = query.batchSize() > 0 ? Math.min( query.batchSize(), MAX_RESULT_WINDOW ) : MAX_RESULT_WINDOW;

        List<Hit> hits = new ArrayList<>();
        long totalHits = 0;
        float maxScore = Float.NaN;
        int from = query.from();

        while ( true )
        {
            Result batch = decode( client.search( target, body( query, from, page ) ), query );
            totalHits = batch.totalHits();
            maxScore = hits.isEmpty() ? batch.maxScore() : maxScore;
            hits.addAll( batch.hits() );

            if ( batch.hits().size() < page || hits.size() >= totalHits )
            {
                break;
            }
            from += page;
            if ( from + page > MAX_RESULT_WINDOW )
            {
                throw new QueryDslTranslator.UnsupportedQueryException(
                    "A GET_ALL query matched more than " + MAX_RESULT_WINDOW + " hits; deep paging over a point-in-time is not implemented" );
            }
        }

        return new Result( hits, totalHits, maxScore );
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
        ObjectNode body = OpenSearchClient.mapper().createObjectNode();

        ObjectNode bool = OpenSearchClient.mapper().createObjectNode();
        bool.set( "must", OpenSearchClient.mapper().createArrayNode().add( QueryDslTranslator.translateQuery( parse( query.query() ) ) ) );

        ArrayNode filter = OpenSearchClient.mapper().createArrayNode();
        filter.add( sourceFilter( query ) );
        for ( String queryFilter : query.queryFilters() )
        {
            filter.add( QueryDslTranslator.translateFilter( parse( queryFilter ) ) );
        }
        bool.set( "filter", filter );

        body.set( "query", OpenSearchClient.mapper().createObjectNode().set( "bool", bool ) );

        if ( !query.postFilters().isEmpty() )
        {
            body.set( "post_filter", combine( query.postFilters() ) );
        }

        if ( !query.sort().isEmpty() )
        {
            ArrayNode sort = OpenSearchClient.mapper().createArrayNode();
            for ( String element : query.sort() )
            {
                sort.add( QueryDslTranslator.translateSort( parse( element ) ) );
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

    private JsonNode combine( List<String> filters )
    {
        if ( filters.size() == 1 )
        {
            return QueryDslTranslator.translateFilter( parse( filters.get( 0 ) ) );
        }
        ArrayNode must = OpenSearchClient.mapper().createArrayNode();
        for ( String filter : filters )
        {
            must.add( QueryDslTranslator.translateFilter( parse( filter ) ) );
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
