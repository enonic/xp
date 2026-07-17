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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Architectural boundary test: {@code org.elasticsearch} types must not leak out of the
 * {@code com.enonic.xp.repo.impl.elasticsearch} package. This confines the storage seam
 * behind the storage SPI (see the storage-spi-phase0 branch).
 * <p>
 * {@link #ALLOWED_LEAKS} is empty as of Gate A: the three pre-existing exception-type
 * leaks (RepositoryCreator, RefreshCommand, NodeServiceImpl) are now translated at the
 * ES-backend boundary (StorageDaoImpl / IndexServiceInternalImpl) into
 * {@code com.enonic.xp.storage.spi} exception types. New code must not add to this set —
 * it must confine ES types to the elasticsearch package instead.
 */
class ElasticsearchImportBoundaryTest
{
    private static final Path SOURCE_ROOT = Path.of( "src", "main", "java" );

    private static final String CONFINED_PACKAGE = "com/enonic/xp/repo/impl/elasticsearch/";

    private static final Pattern ES_IMPORT = Pattern.compile( "^import org\\.elasticsearch\\..*;$" );

    private static final Set<String> ALLOWED_LEAKS = Set.of();

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

    private static String normalize( final Path path )
    {
        return path.toString().replace( '\\', '/' );
    }

    private static boolean importsElasticsearch( final Path path )
    {
        try (Stream<String> lines = Files.lines( path ))
        {
            return lines.anyMatch( line -> ES_IMPORT.matcher( line.strip() ).matches() );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }
}
