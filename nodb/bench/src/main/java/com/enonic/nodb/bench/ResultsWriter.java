package com.enonic.nodb.bench;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Renders a {@link BenchResult} into {@code bench/RESULTS.md} (BUILD-SLICE-1.md step 6 /
 * DESIGN.md §10 risk #2).
 *
 * <p>Phase 4 Gate G changed the write discipline: the Phase-1 numbers already in the file are a
 * frozen historical record, so instead of overwriting the whole file this writer preserves
 * everything above the {@code phase4-baseline} marker and replaces only the marker section with
 * the current run. Re-running the bench is therefore idempotent on the Phase-1 record.
 */
final class ResultsWriter
{
    private static final String MARKER = "<!-- phase4-baseline -->";

    private ResultsWriter()
    {
    }

    static void write( Path path, BenchResult result )
    {
        try
        {
            String head;
            if ( Files.exists( path ) )
            {
                String existing = Files.readString( path, StandardCharsets.UTF_8 );
                int marker = existing.indexOf( MARKER );
                head = ( marker >= 0 ? existing.substring( 0, marker ) : existing ).stripTrailing();
            }
            else
            {
                head = "# NoDB bench results";
            }
            Files.writeString( path, head + "\n\n" + MARKER + "\n" + renderPhase4Section( result ), StandardCharsets.UTF_8 );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    private static String renderPhase4Section( BenchResult result )
    {
        BenchConfig config = result.config();
        StringBuilder sb = new StringBuilder();
        sb.append( "## Phase 4 baseline — the complete PG + OpenSearch path (Gate G)\n\n" );
        sb.append( "Recorded " )
            .append( DateTimeFormatter.ISO_INSTANT.format( Instant.now() ) )
            .append( " by `BenchHarness`. **A BASELINE, NOT AN SLO.**\n\n" );

        sb.append( "### Environment / caveat\n\n" );
        sb.append( "- Everything on ONE dev laptop over loopback: bench client + `NodbServer` in one JVM (real TCP socket),\n" +
                       "  `postgres:17` and stock `opensearchproject/opensearch:3.7.0` (512m heap) in Testcontainers on the same\n" +
                       "  Docker daemon. This measures gRPC + serialization + real PG/OpenSearch round-trips, NOT cross-host\n" +
                       "  network latency — the honest cross-host numbers can only come from a real deployment later; do not\n" +
                       "  quote these as such.\n" );
        sb.append( "- OpenSearch runs the PRODUCTION default `refresh_interval` (1s), unlike the engine tests which pin `-1`.\n" );
        sb.append( "- The outbox indexer drains concurrently with seeding (poll 100ms, batch 500), so \"index drain\" below is\n" +
                       "  the residual `awaitRefresh` barrier after the last shipped document, not the full indexing cost.\n\n" );

        sb.append( "### Corpus\n\n" );
        sb.append( "- Nodes seeded in PG: " ).append( result.seedNodeCount() ).append( " (" ).append( config.folderCount() )
            .append( " folders x (1 + " ).append( config.childrenPerFolder() ).append( " children)), WriteBatch " )
            .append( config.writeBatchSize() ).append( " nodes/call\n" );
        sb.append( "- Search documents shipped (`IndexDocuments`, same batch size): " ).append( result.searchDocCount() )
            .append( " — each with a 6-word title, 150-word body (64-word vocabulary), one of 20 categories, a numeric price,\n" +
                         "  a timestamp and an ACL read key; text fields shipped as both bare and `._analyzed` variants,\n" +
                         "  mirroring the XP index-document shape\n" );
        sb.append( "- Seed wall-clock: " ).append( result.seedWallMillis() ).append( " ms (" )
            .append( String.format( "%.0f", result.nodesPerSecond() ) )
            .append( " nodes/sec, PG write + search-document ship combined)\n" );
        sb.append( "- Index drain after seeding (final `awaitRefresh` barrier): " ).append( result.indexDrainMillis() )
            .append( " ms\n" );
        sb.append( "- Note on the fulltext rows: with 150-word bodies drawn from a 64-word vocabulary, an OR fulltext over\n" +
                       "  title+body matches (and scores) nearly the ENTIRE corpus — those rows are the match-everything worst\n" +
                       "  case, not a selective-query number; the term row (1-in-20 categories) is the selective counterpart.\n\n" );

        sb.append( "### Latency (client-observed, `System.nanoTime()` around each blocking call; warmup " )
            .append( config.warmupOps() ).append( " ops discarded, " ).append( config.measuredOps() )
            .append( " ops measured per row)\n\n" );
        appendTable( sb, result.opStats() );

        sb.append( "\n### FINDINGS #7 — highlight `type: plain` (current, forced) vs engine default `unified`\n\n" );
        sb.append( "Same query stream (fixed seed), same alias, same three-field expansion with `require_field_match: false`,\n" +
                       "issued directly at OpenSearch so the ONLY variable is the highlighter type:\n\n" );
        appendTable( sb, result.highlightStats() );
        if ( result.highlightStats().size() == 2 )
        {
            LatencyStats plain = result.highlightStats().get( 0 );
            LatencyStats unified = result.highlightStats().get( 1 );
            sb.append( String.format( Locale.ROOT, "%nMeasured ratio plain/unified: %.2fx at p50, %.2fx at mean.%n",
                                       plain.p50Micros() / unified.p50Micros(), plain.meanMicros() / unified.meanMicros() ) );
        }

        sb.append( "\n### Re-running this section\n\n" );
        sb.append( "`../gradlew :bench:run` (or `:bench:test -Dnodb.bench.full=true --tests \"*BenchHarnessTest\"`) reruns the\n" +
                       "full bench and REPLACES everything from the `phase4-baseline` marker down; the Phase-1 record above the\n" +
                       "marker is preserved verbatim. Plain `../gradlew build` runs the reduced (~5k node) config and does not\n" +
                       "touch this file.\n" );
        return sb.toString();
    }

    private static void appendTable( StringBuilder sb, List<LatencyStats> rows )
    {
        sb.append( "| Operation | p50 (µs) | p95 (µs) | p99 (µs) | mean (µs) | n |\n" );
        sb.append( "|---|---:|---:|---:|---:|---:|\n" );
        for ( LatencyStats stats : rows )
        {
            sb.append( "| " ).append( stats.operation() ).append( " | " )
                .append( String.format( "%.0f", stats.p50Micros() ) ).append( " | " )
                .append( String.format( "%.0f", stats.p95Micros() ) ).append( " | " )
                .append( String.format( "%.0f", stats.p99Micros() ) ).append( " | " )
                .append( String.format( "%.0f", stats.meanMicros() ) ).append( " | " )
                .append( stats.count() ).append( " |\n" );
        }
    }
}
