package com.enonic.xp.itest;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Phase 4 Gate F (nodb/BUILD-PHASE-4.md): the POSITIVE, mechanical proof that no embedded
 * Elasticsearch node ever boots in nodb mode.
 * <p>
 * The gate's claim is not "no query reached Elasticsearch" but "the node itself never
 * started", and the difference matters: three separate times in this phase a harness silently
 * pointed at the wrong engine (nodb/FINDINGS.md), and "no embedded ES in nodb mode" is exactly
 * the claim a stale wiring would fake. An absence of failures proves nothing here, so this
 * probe asserts four INDEPENDENT observations, three of which are made against artifacts
 * Elasticsearch's own code produces rather than against bookkeeping this fixture keeps:
 * <ol>
 * <li><b>Threads.</b> ES 2.4 names every thread-pool thread
 * {@code elasticsearch[<node.name>][<pool>]} ({@code EsExecutors#threadName}), and
 * {@code Node#start()} cannot serve anything without those pools. No live thread whose name
 * starts with {@code elasticsearch} therefore means no node is running.</li>
 * <li><b>The system property.</b> {@link EmbeddedElasticsearchServer}'s constructor sets
 * {@code mapper.allow_dots_in_name} as its FIRST statement, before it computes a single path,
 * so a non-null value proves that constructor ran — even if the node later failed to start or
 * was shut down and its threads reaped.</li>
 * <li><b>The data directory.</b> The same constructor creates a
 * {@code elasticsearchFixture*} temp directory and an {@code elasticsearch-data} tree inside
 * it. Any such directory created after this JVM started (older ones can survive a crashed
 * run) means the fixture built an ES home.</li>
 * <li><b>The published client.</b> {@link AbstractElasticsearchIntegrationTest#client} is the
 * fixture's only publication point; a non-null value means a node handed one out.</li>
 * </ol>
 * Checks 1–3 would fire even if this fixture's own mode branching were reverted or bypassed,
 * which is the property that makes them a proof rather than a restatement of the branch.
 * <p>
 * Called from {@link AbstractElasticsearchIntegrationTest}'s JUnit extension, i.e. once per
 * test class, at exactly the point where the node would otherwise have been booted — so every
 * class in both itest suites re-proves it, not just a dedicated test.
 */
public final class NoEmbeddedElasticsearchProbe
{
    /** Set by {@link EmbeddedElasticsearchServer}'s constructor before it does anything else. */
    private static final String ES_SYSTEM_PROPERTY = "mapper.allow_dots_in_name";

    /** {@code Files.createTempDirectory} prefix used by {@link AbstractElasticsearchIntegrationTest.ElasticsearchFixture}. */
    private static final String ES_TEMP_DIR_PREFIX = "elasticsearchFixture";

    private static final Instant JVM_START = ProcessHandle.current().info().startInstant().orElse( Instant.EPOCH );

    private NoEmbeddedElasticsearchProbe()
    {
    }

    /**
     * Fails with a description of every violated observation, or returns silently. Deliberately
     * throws {@link AssertionError} rather than using an assertion library: it runs from a JUnit
     * extension callback, where a failed assertion must abort the class.
     */
    public static void assertNoEmbeddedElasticsearch( final String context )
    {
        final List<String> violations = evidenceOfEmbeddedElasticsearch();
        if ( !violations.isEmpty() )
        {
            throw new AssertionError( "nodb mode must run ZERO embedded Elasticsearch (Phase 4 Gate F), but " + context + ": " +
                                          String.join( "; ", violations ) );
        }
    }

    /**
     * Every observation that indicates an embedded node exists, empty when none do. Public so a
     * test can assert the probe is NOT vacuous: in default mode, where a node genuinely is running,
     * this must come back non-empty.
     */
    public static List<String> evidenceOfEmbeddedElasticsearch()
    {
        final List<String> violations = new ArrayList<>();

        final Set<String> esThreads = liveElasticsearchThreads();
        if ( !esThreads.isEmpty() )
        {
            violations.add( "live Elasticsearch thread-pool threads exist: " + esThreads );
        }

        final String esProperty = System.getProperty( ES_SYSTEM_PROPERTY );
        if ( esProperty != null )
        {
            violations.add( "system property [" + ES_SYSTEM_PROPERTY + "] is set to [" + esProperty +
                                "], which only EmbeddedElasticsearchServer's constructor does" );
        }

        final List<Path> dataDirs = elasticsearchDataDirectoriesCreatedByThisJvm();
        if ( !dataDirs.isEmpty() )
        {
            violations.add( "an embedded Elasticsearch home was created by this JVM: " + dataDirs );
        }

        if ( AbstractElasticsearchIntegrationTest.client != null )
        {
            violations.add( "AbstractElasticsearchIntegrationTest.client is non-null (" +
                                AbstractElasticsearchIntegrationTest.client.getClass().getName() + ")" );
        }

        return violations;
    }

    /** ES 2.4 thread naming is {@code elasticsearch[<node.name>][<pool>]} — see {@code EsExecutors#threadName}. */
    private static Set<String> liveElasticsearchThreads()
    {
        return Thread.getAllStackTraces()
            .keySet()
            .stream()
            .map( Thread::getName )
            .filter( name -> name.startsWith( "elasticsearch" ) )
            .collect( java.util.stream.Collectors.toCollection( java.util.TreeSet::new ) );
    }

    private static List<Path> elasticsearchDataDirectoriesCreatedByThisJvm()
    {
        final Path tmp = Path.of( System.getProperty( "java.io.tmpdir" ) );
        if ( !Files.isDirectory( tmp ) )
        {
            return List.of();
        }
        try (Stream<Path> entries = Files.list( tmp ))
        {
            return entries.filter( path -> path.getFileName().toString().startsWith( ES_TEMP_DIR_PREFIX ) )
                .filter( NoEmbeddedElasticsearchProbe::createdAfterJvmStart )
                .toList();
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    private static boolean createdAfterJvmStart( final Path path )
    {
        try
        {
            return !Files.readAttributes( path, BasicFileAttributes.class ).creationTime().toInstant().isBefore( JVM_START );
        }
        catch ( IOException e )
        {
            // A directory we cannot stat is not evidence either way; the other three checks stand.
            return false;
        }
    }
}
