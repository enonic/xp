package com.enonic.xp.core.nodb.corpus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Reads and writes the committed baseline artifact.
 * <p>
 * The artifact is JSON, but every choice here is about being <em>diffable</em> rather than about
 * being JSON: queries are written sorted by id, keys are emitted in a fixed order (Jackson's
 * {@code ObjectNode} preserves insertion order), the default pretty printer puts one scalar per
 * line, and nothing is written whose order the engine did not decide (highlight fragments,
 * aggregation names and metric keys are sorted upstream in {@link CorpusRecorder}). A
 * one-line-per-value pretty file means a git diff of a re-record points straight at the query and
 * the field that moved.
 */
final class CorpusArtifact
{
    static final int FORMAT_VERSION = 1;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private CorpusArtifact()
    {
    }

    static void write( final Path file, final String mode, final List<QueryOutcome> outcomes )
        throws IOException
    {
        final ObjectNode root = MAPPER.createObjectNode();
        root.put( "formatVersion", FORMAT_VERSION );
        root.put( "mode", mode );
        root.put( "queryCount", outcomes.size() );

        final ArrayNode queries = root.putArray( "queries" );
        outcomes.stream().sorted( Comparator.comparing( QueryOutcome::id ) ).forEach( outcome -> queries.add( toNode( outcome ) ) );

        Files.createDirectories( file.getParent() );
        Files.writeString( file, MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString( root ) + "\n", StandardCharsets.UTF_8 );
    }

    static List<QueryOutcome> read( final Path file )
        throws IOException
    {
        final JsonNode root = MAPPER.readTree( Files.readString( file, StandardCharsets.UTF_8 ) );
        final int version = root.path( "formatVersion" ).asInt( -1 );
        if ( version != FORMAT_VERSION )
        {
            throw new IOException(
                "Baseline " + file + " has formatVersion " + version + ", this harness writes " + FORMAT_VERSION + " -- re-record it" );
        }
        final List<QueryOutcome> outcomes = new ArrayList<>();
        for ( final JsonNode query : root.path( "queries" ) )
        {
            outcomes.add( fromNode( query ) );
        }
        return List.copyOf( outcomes );
    }

    // ------------------------------------------------------------------ write

    private static ObjectNode toNode( final QueryOutcome outcome )
    {
        final ObjectNode node = MAPPER.createObjectNode();
        node.put( "id", outcome.id() );
        node.put( "family", outcome.family() );
        node.put( "acceptance", outcome.acceptance() );
        node.put( "source", outcome.source() );
        node.put( "intent", outcome.intent() );
        node.put( "error", outcome.error() );
        node.put( "totalHits", outcome.totalHits() );
        node.put( "maxScore", outcome.maxScore() );

        final ArrayNode hits = node.putArray( "hits" );
        for ( final QueryOutcome.Hit hit : outcome.hits() )
        {
            final ObjectNode h = hits.addObject();
            h.put( "id", hit.id() );
            h.put( "path", hit.path() );
            h.put( "score", hit.score() );
            h.put( "index", hit.index() );
            h.put( "type", hit.type() );
            strings( h.putArray( "sort" ), hit.sort() );
            final ArrayNode highlights = h.putArray( "highlight" );
            for ( final QueryOutcome.Highlight highlight : hit.highlight() )
            {
                final ObjectNode hl = highlights.addObject();
                hl.put( "name", highlight.name() );
                strings( hl.putArray( "fragments" ), highlight.fragments() );
            }
        }

        aggregations( node.putArray( "aggregations" ), outcome.aggregations() );

        final ArrayNode suggestions = node.putArray( "suggestions" );
        for ( final QueryOutcome.Suggest suggest : outcome.suggestions() )
        {
            final ObjectNode s = suggestions.addObject();
            s.put( "name", suggest.name() );
            final ArrayNode entries = s.putArray( "entries" );
            for ( final QueryOutcome.SuggestEntry entry : suggest.entries() )
            {
                final ObjectNode e = entries.addObject();
                e.put( "text", entry.text() );
                e.put( "offset", entry.offset() );
                e.put( "length", entry.length() );
                final ArrayNode options = e.putArray( "options" );
                for ( final QueryOutcome.SuggestOption option : entry.options() )
                {
                    final ObjectNode o = options.addObject();
                    o.put( "text", option.text() );
                    o.put( "score", option.score() );
                    o.put( "freq", option.freq() );
                }
            }
        }

        return node;
    }

    private static void aggregations( final ArrayNode target, final List<QueryOutcome.Agg> aggregations )
    {
        for ( final QueryOutcome.Agg agg : aggregations )
        {
            final ObjectNode a = target.addObject();
            a.put( "name", agg.name() );
            a.put( "kind", agg.kind() );
            strings( a.putArray( "metrics" ), agg.metrics() );
            final ArrayNode buckets = a.putArray( "buckets" );
            for ( final QueryOutcome.Bucket bucket : agg.buckets() )
            {
                final ObjectNode b = buckets.addObject();
                b.put( "key", bucket.key() );
                b.put( "docCount", bucket.docCount() );
                aggregations( b.putArray( "subAggregations" ), bucket.subAggregations() );
            }
        }
    }

    private static void strings( final ArrayNode target, final List<String> values )
    {
        values.forEach( target::add );
    }

    // ------------------------------------------------------------------ read

    private static QueryOutcome fromNode( final JsonNode node )
    {
        final List<QueryOutcome.Hit> hits = new ArrayList<>();
        for ( final JsonNode h : node.path( "hits" ) )
        {
            final List<QueryOutcome.Highlight> highlights = new ArrayList<>();
            for ( final JsonNode hl : h.path( "highlight" ) )
            {
                highlights.add( new QueryOutcome.Highlight( hl.path( "name" ).asText(), stringList( hl.path( "fragments" ) ) ) );
            }
            hits.add( new QueryOutcome.Hit( h.path( "id" ).asText(), text( h, "path" ), h.path( "score" ).asText(),
                                            stringList( h.path( "sort" ) ), text( h, "index" ), text( h, "type" ),
                                            List.copyOf( highlights ) ) );
        }

        final List<QueryOutcome.Suggest> suggestions = new ArrayList<>();
        for ( final JsonNode s : node.path( "suggestions" ) )
        {
            final List<QueryOutcome.SuggestEntry> entries = new ArrayList<>();
            for ( final JsonNode e : s.path( "entries" ) )
            {
                final List<QueryOutcome.SuggestOption> options = new ArrayList<>();
                for ( final JsonNode o : e.path( "options" ) )
                {
                    options.add( new QueryOutcome.SuggestOption( o.path( "text" ).asText(), o.path( "score" ).asText(),
                                                                 integer( o, "freq" ) ) );
                }
                entries.add( new QueryOutcome.SuggestEntry( e.path( "text" ).asText(), integer( e, "offset" ), integer( e, "length" ),
                                                            List.copyOf( options ) ) );
            }
            suggestions.add( new QueryOutcome.Suggest( s.path( "name" ).asText(), List.copyOf( entries ) ) );
        }

        return new QueryOutcome( node.path( "id" ).asText(), node.path( "family" ).asText(), node.path( "acceptance" ).asText(),
                                 node.path( "source" ).asText(), node.path( "intent" ).asText(), node.path( "totalHits" ).asLong(),
                                 node.path( "maxScore" ).asText(), List.copyOf( hits ), aggList( node.path( "aggregations" ) ),
                                 List.copyOf( suggestions ), text( node, "error" ) );
    }

    private static List<QueryOutcome.Agg> aggList( final JsonNode array )
    {
        final List<QueryOutcome.Agg> out = new ArrayList<>();
        for ( final JsonNode a : array )
        {
            final List<QueryOutcome.Bucket> buckets = new ArrayList<>();
            for ( final JsonNode b : a.path( "buckets" ) )
            {
                buckets.add( new QueryOutcome.Bucket( b.path( "key" ).asText(), b.path( "docCount" ).asLong(),
                                                      aggList( b.path( "subAggregations" ) ) ) );
            }
            out.add( new QueryOutcome.Agg( a.path( "name" ).asText(), a.path( "kind" ).asText(), stringList( a.path( "metrics" ) ),
                                           List.copyOf( buckets ) ) );
        }
        return List.copyOf( out );
    }

    /** Not {@code List.copyOf}: a sort value can legitimately be null and must round-trip as one. */
    private static List<String> stringList( final JsonNode array )
    {
        final List<String> out = new ArrayList<>();
        array.forEach( n -> out.add( n.isNull() ? null : n.asText() ) );
        return java.util.Collections.unmodifiableList( out );
    }

    private static String text( final JsonNode node, final String field )
    {
        final JsonNode value = node.path( field );
        return value.isMissingNode() || value.isNull() ? null : value.asText();
    }

    private static Integer integer( final JsonNode node, final String field )
    {
        final JsonNode value = node.path( field );
        return value.isMissingNode() || value.isNull() ? null : value.asInt();
    }
}
