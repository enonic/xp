package com.enonic.gradle;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import groovy.json.JsonOutput;
import groovy.json.JsonSlurper;

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

/** Build-only assembly of pinned macOS ARM64 libraries. Never modifies Mach-O bytes or creates filesystem links. */
public final class MacImageMagickPackager
{
    private final Map<String, Path> files = new HashMap<>();

    private final Map<String, String> links = new HashMap<>();

    private final Map<String, String> owners = new HashMap<>();

    private final long deadline = System.nanoTime() + TimeUnit.MINUTES.toNanos( 10 );

    public static void packageBundle( final Path manifest, final Path target, final String sevenZip, final Path cache )
        throws Exception
    {
        new MacImageMagickPackager().assemble( manifest, target.toAbsolutePath(), sevenZip, cache );
    }

    @SuppressWarnings("unchecked")
    private void assemble( final Path manifest, final Path target, final String sevenZip, final Path cache )
        throws Exception
    {
        final Map<String, Object> data = (Map<String, Object>) new JsonSlurper().parse( manifest.toFile(), "UTF-8" );
        final List<Map<String, String>> packages = (List<Map<String, String>>) data.get( "packages" );
        Files.createDirectories( cache );
        Files.createDirectories( target.getParent() );
        final Path work = Files.createTempDirectory( target.getParent(), "macos-packaging-" );
        try
        {
            final List<Path> downloads = downloadAll( packages, cache );
            for ( int i = 0; i < packages.size(); i++ )
            {
                remainingSeconds();
                unpack( downloads.get( i ), packages.get( i ).get( "name" ), sevenZip, work );
            }
            final Set<String> selected = selectLibraries();
            final Set<String> includedPackages = new HashSet<>();
            selected.forEach( name -> includedPackages.add( owners.get( name ) ) );
            files.keySet().stream().filter( name -> name.startsWith( "etc/ImageMagick" ) ||
                name.startsWith( "licenses/" ) && includedPackages.contains( owners.get( name ) ) ).forEach( selected::add );
            final String metadata = JsonOutput.prettyPrint( JsonOutput.toJson(
                packages.stream().filter( item -> includedPackages.contains( item.get( "name" ) ) ).toList() ) );
            final Path archive = work.resolve( "bundle.zip" );
            try (var zip = new ZipOutputStream( Files.newOutputStream( archive ) ))
            {
                zip.setLevel( 9 );
                for ( String name : selected )
                {
                    remainingSeconds();
                    zip.putNextEntry( zipEntry( name ) );
                    Files.copy( contents( name ), zip );
                    zip.closeEntry();
                }
                zip.putNextEntry( zipEntry( "packages.json" ) );
                zip.write( metadata.getBytes( StandardCharsets.UTF_8 ) );
                zip.closeEntry();
            }
            Files.move( archive, target, StandardCopyOption.REPLACE_EXISTING );
            System.out.println( "Bundled macOS ARM64 ImageMagick: " + selected.size() + " files, " + Files.size( target ) + " bytes" );
        }
        finally
        {
            delete( work );
        }
    }

    private List<Path> downloadAll( final List<Map<String, String>> packages, final Path cache )
        throws Exception
    {
        final var executor = Executors.newFixedThreadPool( 6 );
        final List<Future<Path>> futures = new ArrayList<>();
        try
        {
            for ( Map<String, String> item : packages )
            {
                futures.add( executor.submit( () -> download( item, cache ) ) );
            }
            final List<Path> paths = new ArrayList<>();
            for ( Future<Path> future : futures )
            {
                paths.add( future.get( remainingSeconds(), TimeUnit.SECONDS ) );
            }
            return paths;
        }
        finally
        {
            futures.forEach( future -> future.cancel( true ) );
            executor.shutdownNow();
            // Connections have finite read timeouts; do not leave build workers using the download cache.
            executor.awaitTermination( 125, TimeUnit.SECONDS );
        }
    }

    private static Path download( final Map<String, String> item, final Path cache )
        throws Exception
    {
        final URI uri = URI.create( item.get( "url" ) );
        final String filename = safeName( uri.getPath().substring( uri.getPath().lastIndexOf( '/' ) + 1 ) );
        final Path path = cache.resolve( filename );
        if ( Files.isRegularFile( path ) && checksum( path ).equals( item.get( "sha256" ) ) )
        {
            return path;
        }
        final Path temporary = Files.createTempFile( cache, "download-", ".tmp" );
        try
        {
            final var connection = uri.toURL().openConnection();
            connection.setConnectTimeout( 120_000 );
            connection.setReadTimeout( 120_000 );
            try (InputStream input = connection.getInputStream())
            {
                Files.copy( input, temporary, StandardCopyOption.REPLACE_EXISTING );
            }
            if ( !checksum( temporary ).equals( item.get( "sha256" ) ) )
            {
                throw new IOException( "Checksum mismatch: " + item.get( "name" ) );
            }
            Files.move( temporary, path, StandardCopyOption.REPLACE_EXISTING );
            return path;
        }
        finally
        {
            Files.deleteIfExists( temporary );
        }
    }

    private static String checksum( final Path path )
        throws Exception
    {
        final MessageDigest digest = MessageDigest.getInstance( "SHA-256" );
        try (var input = new DigestInputStream( Files.newInputStream( path ), digest ))
        {
            input.transferTo( java.io.OutputStream.nullOutputStream() );
        }
        return HexFormat.of().formatHex( digest.digest() );
    }

    private void unpack( final Path path, final String owner, final String sevenZip, final Path work )
        throws Exception
    {
        if ( path.toString().endsWith( ".tar.bz2" ) )
        {
            try (var input = new BZip2CompressorInputStream( new BufferedInputStream( Files.newInputStream( path ) ) ))
            {
                readTar( input, owner, work );
            }
        }
        else
        {
            try (var archive = new ZipFile( path.toFile() ))
            {
                final var entries = archive.entries();
                while ( entries.hasMoreElements() )
                {
                    final var entry = entries.nextElement();
                    if ( !entry.getName().endsWith( ".tar.zst" ) )
                    {
                        continue;
                    }
                    final Path compressed = work.resolve( "package.tar.zst" );
                    final Path tar = work.resolve( "package.tar" );
                    try (var input = archive.getInputStream( entry ))
                    {
                        Files.copy( input, compressed, StandardCopyOption.REPLACE_EXISTING );
                    }
                    try
                    {
                        decompress( compressed, tar, sevenZip );
                        try (var input = Files.newInputStream( tar ))
                        {
                            readTar( input, owner, work );
                        }
                    }
                    finally
                    {
                        Files.deleteIfExists( compressed );
                        Files.deleteIfExists( tar );
                    }
                }
            }
        }
    }

    private void decompress( final Path compressed, final Path tar, final String sevenZip )
        throws Exception
    {
        final Path errors = tar.resolveSibling( "decompression.log" );
        Process process;
        try
        {
            process = new ProcessBuilder( "zstd", "-d", "-c", compressed.toString() )
                .redirectOutput( tar.toFile() ).redirectError( errors.toFile() ).start();
        }
        catch ( IOException noZstd )
        {
            process = new ProcessBuilder( sevenZip, "x", "-so", compressed.toString() )
                .redirectOutput( tar.toFile() ).redirectError( errors.toFile() ).start();
        }
        try
        {
            process.getOutputStream().close();
            if ( !process.waitFor( Math.min( 120, remainingSeconds() ), TimeUnit.SECONDS ) )
            {
                throw new IOException( "ImageMagick archive decompression timed out" );
            }
            if ( process.exitValue() != 0 )
            {
                throw new IOException( "ImageMagick archive decompression failed: " + Files.readString( errors ) );
            }
        }
        finally
        {
            if ( process.isAlive() )
            {
                process.descendants().forEach( ProcessHandle::destroyForcibly );
                process.destroyForcibly().waitFor();
            }
            Files.deleteIfExists( errors );
        }
    }

    private void readTar( final InputStream input, final String owner, final Path work )
        throws Exception
    {
        try (var tar = new TarArchiveInputStream( new BufferedInputStream( input ) ))
        {
            org.apache.commons.compress.archivers.tar.TarArchiveEntry member;
            while ( ( member = tar.getNextEntry() ) != null )
            {
                remainingSeconds();
                String name = safeName( member.getName() );
                if ( name.startsWith( "info/licenses/" ) )
                {
                    name = "licenses/" + safeName( owner ) + "/" + name.substring( "info/licenses/".length() );
                }
                if ( !( name.equals( "bin/magick" ) || name.startsWith( "lib/" ) ||
                    name.startsWith( "etc/ImageMagick" ) || name.startsWith( "licenses/" ) ) )
                {
                    continue;
                }
                if ( member.isSymbolicLink() || member.isLink() )
                {
                    final String target = member.getLinkName();
                    // Absolute targets must be rejected before joining with the member's directory.
                    if ( target.startsWith( "/" ) ) throw new IOException( "Unsafe archive link: " + target );
                    links.put( name, safeName( member.isSymbolicLink() ? parent( name ) + target : target ) );
                    files.remove( name );
                }
                else if ( member.isFile() )
                {
                    // Store bytes under generated paths. Archive paths and links never reach filesystem extraction.
                    final Path file = Files.createTempFile( work, "entry-", ".bin" );
                    Files.copy( tar, file, StandardCopyOption.REPLACE_EXISTING );
                    files.put( name, file );
                    links.remove( name );
                }
                owners.put( name, owner );
            }
        }
    }

    private Set<String> selectLibraries()
        throws Exception
    {
        final Set<String> selected = new TreeSet<>();
        final ArrayDeque<String> pending = new ArrayDeque<>();
        pending.add( "bin/magick" );
        files.keySet().stream().filter( name -> name.startsWith( "lib/libheif/" ) &&
            ( name.endsWith( ".so" ) || name.endsWith( ".dylib" ) ) ).forEach( pending::add );
        while ( !pending.isEmpty() )
        {
            final String name = pending.removeLast();
            if ( !selected.add( name ) ) continue;
            for ( String dependency : dependencies( Files.readAllBytes( contents( name ) ) ) )
            {
                if ( dependency.startsWith( "/usr/lib/" ) || dependency.startsWith( "/System/Library/" ) ) continue;
                if ( dependency.startsWith( "@rpath/" ) )
                {
                    pending.add( safeName( "lib/" + dependency.substring( "@rpath/".length() ) ) );
                }
                else if ( dependency.startsWith( "@loader_path/" ) )
                {
                    pending.add( safeName( parent( name ) + dependency.substring( "@loader_path/".length() ) ) );
                }
                else
                {
                    throw new IOException( "Non-relocatable dependency in " + name + ": " + dependency );
                }
            }
        }
        return selected;
    }

    private Path contents( String name )
        throws IOException
    {
        final Set<String> seen = new HashSet<>();
        while ( links.containsKey( name ) )
        {
            if ( !seen.add( name ) ) throw new IOException( "Archive link cycle: " + name );
            name = links.get( name );
        }
        final Path path = files.get( name );
        if ( path == null ) throw new IOException( "Missing library or archive link target: " + name );
        return path;
    }

    static List<String> dependencies( final byte[] data )
        throws IOException
    {
        final List<String> result = new ArrayList<>();
        if ( data.length < 4 || ByteBuffer.wrap( data ).order( ByteOrder.LITTLE_ENDIAN ).getInt() != 0xfeedfacf ) return result;
        if ( data.length < 32 ) throw new IOException( "Truncated Mach-O header" );
        final ByteBuffer buffer = ByteBuffer.wrap( data ).order( ByteOrder.LITTLE_ENDIAN );
        if ( buffer.getInt( 4 ) != 0x0100000c ) throw new IOException( "Expected ARM64 Mach-O" );
        final int count = buffer.getInt( 16 );
        final int commandBytes = buffer.getInt( 20 );
        if ( count < 0 || commandBytes < 0 || commandBytes > data.length - 32 ) throw new IOException( "Invalid Mach-O commands" );
        final int end = 32 + commandBytes;
        int position = 32;
        for ( int i = 0; i < count; i++ )
        {
            if ( position > end - 8 ) throw new IOException( "Truncated Mach-O command" );
            final int command = buffer.getInt( position );
            final int size = buffer.getInt( position + 4 );
            if ( size < 8 || size > end - position ) throw new IOException( "Invalid Mach-O command" );
            if ( command == 0xc || command == 0x80000018 || command == 0x8000001f || command == 0x80000023 || command == 0x20 )
            {
                if ( size < 24 ) throw new IOException( "Invalid Mach-O dylib command" );
                final int offset = buffer.getInt( position + 8 );
                if ( offset < 24 || offset >= size ) throw new IOException( "Invalid Mach-O dylib name" );
                int zero = position + offset;
                while ( zero < position + size && data[zero] != 0 ) zero++;
                if ( zero == position + size ) throw new IOException( "Unterminated Mach-O dylib name" );
                result.add( new String( data, position + offset, zero - position - offset, StandardCharsets.UTF_8 ) );
            }
            position += size;
        }
        return result;
    }

    static String safeName( final String name )
        throws IOException
    {
        if ( name.startsWith( "/" ) || name.contains( "\\" ) || name.contains( ":" ) || name.indexOf( '\0' ) >= 0 )
        {
            throw new IOException( "Unsafe archive path: " + name );
        }
        final ArrayDeque<String> parts = new ArrayDeque<>();
        for ( String part : name.split( "/" ) )
        {
            if ( part.isEmpty() || part.equals( "." ) ) continue;
            if ( part.equals( ".." ) )
            {
                if ( parts.isEmpty() ) throw new IOException( "Unsafe archive path: " + name );
                parts.removeLast();
            }
            else parts.add( part );
        }
        return String.join( "/", parts );
    }

    private static String parent( final String name )
    {
        return name.substring( 0, name.lastIndexOf( '/' ) + 1 );
    }

    private static ZipEntry zipEntry( final String name )
    {
        final ZipEntry entry = new ZipEntry( name );
        entry.setTimeLocal( LocalDateTime.of( 1980, 1, 2, 0, 0 ) );
        return entry;
    }

    private long remainingSeconds()
        throws IOException
    {
        final long seconds = TimeUnit.NANOSECONDS.toSeconds( deadline - System.nanoTime() );
        if ( seconds < 1 || Thread.currentThread().isInterrupted() ) throw new IOException( "ImageMagick packaging timed out or interrupted" );
        return seconds;
    }

    private static void delete( final Path directory )
        throws IOException
    {
        try (var paths = Files.walk( directory ))
        {
            for ( Path path : paths.sorted( Comparator.reverseOrder() ).toList() ) Files.deleteIfExists( path );
        }
    }
}
