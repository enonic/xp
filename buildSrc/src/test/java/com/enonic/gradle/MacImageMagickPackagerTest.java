package com.enonic.gradle;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

import groovy.json.JsonOutput;
import groovy.json.JsonSlurper;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarConstants;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MacImageMagickPackagerTest
{
    @TempDir
    Path temporaryFolder;

    @Test
    void assemblesLibraryClosureMaterializesLinksAndRetainsLicensesReproducibly()
        throws Exception
    {
        final byte[] executable = macho( "@rpath/libfoo.dylib" );
        final byte[] library = macho( "@loader_path/libbar.dylib" );
        final byte[] systemOnly = macho( "/usr/lib/libSystem.B.dylib" );
        final Map<String, String> main = archive( "main", List.of(
            file( "bin/magick", executable ), file( "etc/ImageMagick-7/policy.xml", "policy".getBytes() ),
            file( "info/licenses/LICENSE", "main license".getBytes() ) ) );
        final Map<String, String> libs = archive( "libs", List.of(
            link( "lib/libfoo.dylib", "libfoo.1.dylib", false ), file( "lib/libfoo.1.dylib", library ),
            link( "lib/libbar.dylib", "lib/libbar.1.dylib", true ), file( "lib/libbar.1.dylib", systemOnly ),
            file( "lib/libheif/codec.so", macho( "@rpath/libfoo.dylib" ) ), file( "info/licenses/LICENSE", "lib license".getBytes() ) ) );
        final Map<String, String> unused = archive( "unused", List.of(
            file( "lib/unused.dylib", systemOnly ), file( "info/licenses/LICENSE", "unused license".getBytes() ) ) );
        final Path manifest = manifest( List.of( main, libs, unused ) );
        final Path output = temporaryFolder.resolve( "output.zip" );
        MacImageMagickPackager.packageBundle( manifest, output, "unused", temporaryFolder.resolve( "cache" ) );
        try (var zip = new ZipFile( output.toFile() ))
        {
            assertArrayEquals( executable, zip.getInputStream( zip.getEntry( "bin/magick" ) ).readAllBytes() );
            assertArrayEquals( library, zip.getInputStream( zip.getEntry( "lib/libfoo.dylib" ) ).readAllBytes() );
            assertArrayEquals( systemOnly, zip.getInputStream( zip.getEntry( "lib/libbar.dylib" ) ).readAllBytes() );
            final Set<String> names = zip.stream().map( e -> e.getName() ).collect( Collectors.toSet() );
            assertEquals( Set.of( "bin/magick", "lib/libfoo.dylib", "lib/libbar.dylib", "lib/libheif/codec.so",
                "etc/ImageMagick-7/policy.xml", "licenses/main/LICENSE", "licenses/libs/LICENSE", "packages.json" ), names );
            final String metadata = new String( zip.getInputStream( zip.getEntry( "packages.json" ) ).readAllBytes(), StandardCharsets.UTF_8 );
            assertEquals( List.of( main, libs ), new JsonSlurper().parseText( metadata ) );
        }
        // Cached archives must suffice after sources disappear; ZIP timestamps must not depend on local timezone.
        Files.delete( temporaryFolder.resolve( "main.tar.bz2" ) );
        Files.delete( temporaryFolder.resolve( "libs.tar.bz2" ) );
        Files.delete( temporaryFolder.resolve( "unused.tar.bz2" ) );
        final Path second = temporaryFolder.resolve( "second.zip" );
        final TimeZone timezone = TimeZone.getDefault();
        try
        {
            TimeZone.setDefault( TimeZone.getTimeZone( "Pacific/Auckland" ) );
            MacImageMagickPackager.packageBundle( manifest, second, "unused", temporaryFolder.resolve( "cache" ) );
        }
        finally
        {
            TimeZone.setDefault( timezone );
        }
        assertArrayEquals( Files.readAllBytes( output ), Files.readAllBytes( second ) );
        assertNoWorkDirectories();
    }

    @Test
    void rejectsWrongChecksumsWithoutPublishing()
        throws Exception
    {
        final Map<String, String> item = archive( "bad", List.of( file( "bin/magick", macho() ) ) );
        item.put( "sha256", "0".repeat( 64 ) );
        final Path output = temporaryFolder.resolve( "output.zip" );
        final Exception error = assertThrows( Exception.class,
            () -> MacImageMagickPackager.packageBundle( manifest( List.of( item ) ), output, "unused", temporaryFolder.resolve( "cache" ) ) );
        assertTrue( error.toString().contains( "Checksum mismatch" ) );
        assertFalse( Files.exists( output ) );
        assertNoWorkDirectories();
    }

    @ParameterizedTest
    @ValueSource(strings = {"../escape", "/absolute", "C:/windows", "lib/../../escape", "lib\\escape"})
    void rejectsUnsafePaths( final String path )
    {
        assertThrows( IOException.class, () -> MacImageMagickPackager.safeName( path ) );
    }

    @ParameterizedTest
    @ValueSource(strings = {"cycle", "missing", "external"})
    void rejectsInvalidDependencyGraphAndCleansUp( final String failure )
        throws Exception
    {
        final List<Member> entries = new ArrayList<>();
        entries.add( file( "bin/magick", macho( "external".equals( failure ) ? "/opt/homebrew/lib/foo.dylib" : "@rpath/foo.dylib" ) ) );
        if ( "cycle".equals( failure ) )
        {
            entries.add( link( "lib/foo.dylib", "bar.dylib", false ) );
            entries.add( link( "lib/bar.dylib", "foo.dylib", false ) );
        }
        final Path manifest = manifest( List.of( archive( "bad", entries ) ) );
        final Path output = temporaryFolder.resolve( "output.zip" );
        assertThrows( IOException.class,
            () -> MacImageMagickPackager.packageBundle( manifest, output, "unused", temporaryFolder.resolve( "cache" ) ) );
        assertFalse( Files.exists( output ) );
        assertNoWorkDirectories();
    }

    @Test
    void rejectsInvalidMachOCommandsAndArchitecture()
        throws Exception
    {
        final byte[] valid = macho( "@rpath/libfoo.dylib" );
        assertEquals( List.of( "@rpath/libfoo.dylib" ), MacImageMagickPackager.dependencies( valid ) );
        assertThrows( IOException.class, () -> MacImageMagickPackager.dependencies( Arrays.copyOf( valid, 31 ) ) );
        final byte[] badCpu = valid.clone();
        ByteBuffer.wrap( badCpu ).order( ByteOrder.LITTLE_ENDIAN ).putInt( 4, 0x01000007 );
        assertThrows( IOException.class, () -> MacImageMagickPackager.dependencies( badCpu ) );
        final byte[] badSize = valid.clone();
        ByteBuffer.wrap( badSize ).order( ByteOrder.LITTLE_ENDIAN ).putInt( 36, Integer.MAX_VALUE );
        assertThrows( IOException.class, () -> MacImageMagickPackager.dependencies( badSize ) );
        final byte[] badOffset = valid.clone();
        ByteBuffer.wrap( badOffset ).order( ByteOrder.LITTLE_ENDIAN ).putInt( 40, -1 );
        assertThrows( IOException.class, () -> MacImageMagickPackager.dependencies( badOffset ) );
    }

    private Path manifest( final List<Map<String, String>> packages )
        throws IOException
    {
        final Path manifest = temporaryFolder.resolve( "manifest.json" );
        Files.writeString( manifest, JsonOutput.toJson( Map.of( "packages", packages ) ) );
        return manifest;
    }

    private Map<String, String> archive( final String name, final List<Member> entries )
        throws Exception
    {
        final Path archive = temporaryFolder.resolve( name + ".tar.bz2" );
        try (var tar = new TarArchiveOutputStream( new BZip2CompressorOutputStream( Files.newOutputStream( archive ) ) ))
        {
            for ( Member member : entries )
            {
                final var entry = new TarArchiveEntry( member.name(), member.type() );
                entry.setSize( member.bytes().length );
                if ( member.link() != null ) entry.setLinkName( member.link() );
                tar.putArchiveEntry( entry );
                tar.write( member.bytes() );
                tar.closeArchiveEntry();
            }
        }
        return new java.util.LinkedHashMap<>( Map.of( "name", name, "version", "1", "url", archive.toUri().toString(),
            "sha256", HexFormat.of().formatHex( MessageDigest.getInstance( "SHA-256" ).digest( Files.readAllBytes( archive ) ) ) ) );
    }

    private record Member(String name, byte[] bytes, byte type, String link) { }

    private static Member file( final String name, final byte[] bytes )
    {
        return new Member( name, bytes, TarConstants.LF_NORMAL, null );
    }

    private static Member link( final String name, final String target, final boolean hard )
    {
        return new Member( name, new byte[0], hard ? TarConstants.LF_LINK : TarConstants.LF_SYMLINK, target );
    }

    private static byte[] macho( final String... dependencies )
    {
        final int size = Arrays.stream( dependencies ).mapToInt( dependency -> 24 + dependency.getBytes( StandardCharsets.UTF_8 ).length + 1 ).sum();
        final var bytes = ByteBuffer.allocate( 32 + size ).order( ByteOrder.LITTLE_ENDIAN );
        bytes.putInt( 0xfeedfacf ).putInt( 0x0100000c );
        bytes.putInt( 16, dependencies.length );
        bytes.putInt( 20, size );
        bytes.position( 32 );
        for ( String dependency : dependencies )
        {
            final byte[] name = dependency.getBytes( StandardCharsets.UTF_8 );
            final int start = bytes.position();
            bytes.putInt( 0xc ).putInt( 24 + name.length + 1 ).putInt( 24 );
            bytes.position( start + 24 );
            bytes.put( name ).put( (byte) 0 );
        }
        return bytes.array();
    }

    private void assertNoWorkDirectories()
        throws IOException
    {
        try (var paths = Files.list( temporaryFolder ))
        {
            assertFalse( paths.anyMatch( path -> path.getFileName().toString().startsWith( "macos-packaging-" ) ) );
        }
    }
}
