package com.enonic.xp.repo.impl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Architectural boundary tests for the storage-SPI extraction (Phase 0, Gate D final —
 * see {@code nodb/BUILD-PHASE-0.md}). Two rules, enforced exactly, no exceptions:
 * <ol>
 * <li>{@code org.elasticsearch} imports are allowed ONLY under
 * {@code com.enonic.xp.repo.impl.elasticsearch} (core-repo scope) —
 * {@link #noElasticsearchImportsOutsideElasticsearchPackage()}. {@link #ALLOWED_LEAKS} is
 * empty as of Gate A: the three pre-existing exception-type leaks (RepositoryCreator,
 * RefreshCommand, NodeServiceImpl) are now translated at the ES-backend boundary
 * (StorageDaoImpl / IndexServiceInternalImpl) into {@code com.enonic.xp.storage.spi}
 * exception types. New code must not add to this set — it must confine ES types to the
 * elasticsearch package instead.</li>
 * <li>The SPI module's own sources ({@code core-storage-spi/src/main/java}) contain no
 * {@code org.elasticsearch} imports and no {@code com.enonic.xp.repo.impl} imports — the
 * SPI never depends on core-repo, in either direction —
 * {@link #spiModuleHasNoElasticsearchOrCoreRepoImports()}.</li>
 * </ol>
 */
class ElasticsearchImportBoundaryTest
{
    private static final Path SOURCE_ROOT = Path.of( "src", "main", "java" );

    private static final String CONFINED_PACKAGE = "com/enonic/xp/repo/impl/elasticsearch/";

    private static final Pattern ES_IMPORT = Pattern.compile( "^import org\\.elasticsearch\\..*;$" );

    private static final Set<String> ALLOWED_LEAKS = Set.of();

    // core-storage-spi is a sibling Gradle project of core-repo; Gradle test tasks run
    // with the project directory as the working directory, so this is relative to
    // modules/core/core-repo.
    private static final Path SPI_SOURCE_ROOT = Path.of( "..", "core-storage-spi", "src", "main", "java" );

    private static final Pattern CORE_REPO_IMPORT = Pattern.compile( "^import com\\.enonic\\.xp\\.repo\\.impl\\..*;$" );

    @Test
    void noElasticsearchImportsOutsideElasticsearchPackage()
        throws IOException
    {
        final List<String> violations;
        try (Stream<Path> files = Files.walk( SOURCE_ROOT ))
        {
            violations = files.filter( path -> path.toString().endsWith( ".java" ) )
                .filter( path -> !normalize( path ).contains( CONFINED_PACKAGE ) )
                .filter( ElasticsearchImportBoundaryTest::importsElasticsearch )
                .map( path -> SOURCE_ROOT.relativize( path ) )
                .map( ElasticsearchImportBoundaryTest::normalize )
                .filter( relativePath -> !ALLOWED_LEAKS.contains( relativePath ) )
                .toList();
        }

        assertTrue( violations.isEmpty(),
                    () -> "New org.elasticsearch imports found outside com.enonic.xp.repo.impl.elasticsearch " +
                        "(and not in the ALLOWED_LEAKS allowlist): " + violations );
    }

    @Test
    void allowedLeaksAreStillAccurate()
        throws IOException
    {
        final List<String> staleEntries;
        try (Stream<Path> files = Files.walk( SOURCE_ROOT ))
        {
            final Set<String> actualLeaks = files.filter( path -> path.toString().endsWith( ".java" ) )
                .filter( path -> !normalize( path ).contains( CONFINED_PACKAGE ) )
                .filter( ElasticsearchImportBoundaryTest::importsElasticsearch )
                .map( path -> SOURCE_ROOT.relativize( path ) )
                .map( ElasticsearchImportBoundaryTest::normalize )
                .collect( Collectors.toSet() );

            staleEntries = ALLOWED_LEAKS.stream().filter( allowed -> !actualLeaks.contains( allowed ) ).toList();
        }

        assertTrue( staleEntries.isEmpty(), () -> "Stale ALLOWED_LEAKS entries no longer importing org.elasticsearch " +
            "(shrink the allowlist): " + staleEntries );
    }

    @Test
    void spiModuleHasNoElasticsearchOrCoreRepoImports()
        throws IOException
    {
        Assumptions.assumeTrue( Files.isDirectory( SPI_SOURCE_ROOT ), () -> "core-storage-spi sources not found at " +
            SPI_SOURCE_ROOT.toAbsolutePath().normalize() +
            " - module layout appears to have changed; skipping the SPI dependency-direction check rather than failing spuriously" );

        final List<String> violations;
        try (Stream<Path> files = Files.walk( SPI_SOURCE_ROOT ))
        {
            violations = files.filter( path -> path.toString().endsWith( ".java" ) )
                .filter( path -> importsElasticsearch( path ) || importsCoreRepoImpl( path ) )
                .map( path -> SPI_SOURCE_ROOT.relativize( path ) )
                .map( ElasticsearchImportBoundaryTest::normalize )
                .toList();
        }

        assertTrue( violations.isEmpty(), () -> "core-storage-spi must not import org.elasticsearch or " +
            "com.enonic.xp.repo.impl (the SPI never depends on core-repo): " + violations );
    }

    private static String normalize( final Path path )
    {
        return path.toString().replace( '\\', '/' );
    }

    private static boolean importsElasticsearch( final Path path )
    {
        return matchesAnyImportLine( path, ES_IMPORT );
    }

    private static boolean importsCoreRepoImpl( final Path path )
    {
        return matchesAnyImportLine( path, CORE_REPO_IMPORT );
    }

    private static boolean matchesAnyImportLine( final Path path, final Pattern pattern )
    {
        try (Stream<String> lines = Files.lines( path ))
        {
            return lines.anyMatch( line -> pattern.matcher( line.strip() ).matches() );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }
}
