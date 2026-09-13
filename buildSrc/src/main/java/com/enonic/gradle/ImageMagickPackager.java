package com.enonic.gradle;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipFile;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import groovy.json.JsonSlurper;

/** Assembles ordinary bundle resources without executing any target-platform binary. */
public final class ImageMagickPackager
{
    private ImageMagickPackager() {}

    /**
     * Downloads verified distributions and writes unpacked resources for every declared platform.
     *
     * @param manifest the distribution manifest
     * @param output the resource directory
     * @param cache the verified download cache
     * @param sevenZip the build host's 7-Zip executable
     * @param dwarfsExtract an explicit DwarFS extractor, or an empty string to use the platform default
     * @throws Exception if downloading, verification or packaging fails
     */
    @SuppressWarnings("unchecked")
    public static void packageDistributions( final Path manifest, final Path output, final Path cache, final String sevenZip, final String dwarfsExtract )
        throws Exception
    {
        final Map<String, Object> data = (Map<String, Object>) new JsonSlurper().parse( manifest.toFile(), "UTF-8" );
        final Map<String, Map<String, String>> platforms = (Map<String, Map<String, String>>) data.get( "platforms" );
        Files.createDirectories( output );
        Files.createDirectories( cache );
        for ( var item : platforms.entrySet() )
        {
            final String platform = safeName( item.getKey() );
            System.out.println( "Packaging ImageMagick: " + platform );
            final Map<String, String> distribution = item.getValue();
            final Path target = Files.createDirectories( output.resolve( platform ) );
            final Path work = Files.createTempDirectory( cache, "unpack-" );
            try
            {
                if ( distribution.containsKey( "packages" ) )
                {
                    final Path archive = work.resolve( "macos.zip" );
                    MacImageMagickPackager.packageBundle( manifest.getParent().resolve( distribution.get( "packages" ) ),
                        archive, sevenZip, cache );
                    unpackZip( archive, target );
                }
                else
                {
                    final String name = distribution.get( "file" );
                    final URI uri = URI.create( distribution.getOrDefault( "url",
                        "https://github.com/ImageMagick/ImageMagick/releases/download/" + data.get( "version" ) + "/" + name ) );
                    Path archive = download( uri, distribution.get( "sha256" ), cache );
                    if ( "dwarfs".equals( distribution.get( "filesystem" ) ) )
                    {
                        unpackDwarfs( archive, target, work, cache, manifest.getParent().resolve( "build-tools.json" ),
                            sevenZip, dwarfsExtract );
                    }
                    else
                    {
                        if ( name.endsWith( ".AppImage" ) )
                        {
                            final Path squashfs = work.resolve( "distribution.squashfs" );
                            copySquashFs( archive, squashfs );
                            archive = squashfs;
                        }
                        unpackSevenZip( archive, target, work, sevenZip );
                    }
                }
                removeUpdateHooks( target );
                writeIndex( target, distribution.get( "executable" ) );
            }
            finally
            {
                delete( work );
            }
        }
    }

    private static Path download( final URI uri, final String expected, final Path cache ) throws Exception
    {
        final Path file = cache.resolve( expected + ".archive" );
        if ( Files.isRegularFile( file ) && checksum( file ).equals( expected ) ) { return file; }
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
            if ( !checksum( temporary ).equals( expected ) ) { throw new IOException( "ImageMagick checksum mismatch: " + uri ); }
            Files.move( temporary, file, StandardCopyOption.REPLACE_EXISTING );
            return file;
        }
        finally { Files.deleteIfExists( temporary ); }
    }

    private static String checksum( final Path file ) throws Exception
    {
        final MessageDigest digest = MessageDigest.getInstance( "SHA-256" );
        try (var input = new DigestInputStream( Files.newInputStream( file ), digest ))
        {
            input.transferTo( OutputStream.nullOutputStream() );
        }
        return HexFormat.of().formatHex( digest.digest() );
    }

    static void copySquashFs( final Path appImage, final Path target ) throws IOException
    {
        // Locate and validate the SquashFS v4 superblock without running an ELF launcher.
        try (var input = new BufferedInputStream( Files.newInputStream( appImage ) ))
        {
            int signature = 0;
            for ( long offset = 0; offset < 32L * 1024 * 1024; offset++ )
            {
                final int value = input.read();
                if ( value < 0 ) { break; }
                signature = ( signature << 8 ) | value;
                if ( signature != 0x68737173 ) { continue; }
                input.mark( 96 );
                final byte[] rest = input.readNBytes( 92 );
                if ( rest.length != 92 ) { break; }
                final ByteBuffer header = ByteBuffer.wrap( rest ).order( ByteOrder.LITTLE_ENDIAN );
                final int blockSize = header.getInt( 8 );
                final long bytesUsed = header.getLong( 36 );
                if ( header.getShort( 24 ) == 4 && header.getShort( 26 ) == 0 &&
                    blockSize >= 4096 && blockSize <= 1048576 && ( blockSize & ( blockSize - 1 ) ) == 0 &&
                    bytesUsed >= 96 && bytesUsed <= Files.size( appImage ) - ( offset - 3 ) )
                {
                    try (OutputStream output = Files.newOutputStream( target ))
                    {
                        output.write( new byte[]{'h', 's', 'q', 's'} );
                        output.write( rest );
                        copyExactly( input, output, bytesUsed - 96 );
                    }
                    return;
                }
                input.reset();
            }
        }
        throw new IOException( "No valid SquashFS v4 filesystem in AppImage: " + appImage );
    }

    private static void unpackZip( final Path archive, final Path target ) throws IOException
    {
        try (var zip = new ZipFile( archive.toFile() ))
        {
            final var entries = zip.entries();
            while ( entries.hasMoreElements() )
            {
                final var entry = entries.nextElement();
                if ( entry.isDirectory() ) { continue; }
                final Path file = target.resolve( safeName( entry.getName() ) );
                Files.createDirectories( file.getParent() );
                try (var input = zip.getInputStream( entry )) { Files.copy( input, file ); }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void unpackDwarfs( final Path archive, final Path target, final Path work, final Path cache,
                                      final Path tools, final String sevenZip, final String override ) throws Exception
    {
        final List<String> command = new ArrayList<>();
        if ( !override.isBlank() )
        {
            command.add( override );
        }
        else if ( System.getProperty( "os.name" ).startsWith( "Mac" ) )
        {
            command.add( "dwarfsextract" );
        }
        else
        {
            final boolean windows = System.getProperty( "os.name" ).startsWith( "Windows" );
            final String platform = windows ? "windows" : "linux-" + switch ( System.getProperty( "os.arch" ) )
            {
                case "amd64", "x86_64" -> "x86_64";
                case "aarch64", "arm64" -> "aarch64";
                default -> throw new IOException( "Set imageMagickDwarfsExtract for this build platform" );
            };
            final Map<String, Map<String, String>> pins = (Map<String, Map<String, String>>) new JsonSlurper()
                .parse( tools.toFile(), "UTF-8" );
            final Map<String, String> pin = pins.get( platform );
            final Path tool = download( URI.create( pin.get( "url" ) ), pin.get( "sha256" ), cache );
            if ( windows )
            {
                final Path directory = Files.createDirectories( work.resolve( "dwarfs-tools" ) );
                final Path unpack = Files.createDirectories( work.resolve( "dwarfs-unpack" ) );
                unpackSevenZip( tool, directory, unpack, sevenZip );
                try (var files = Files.walk( directory ))
                {
                    command.add( files.filter( path -> path.getFileName().toString().equals( "dwarfsextract.exe" ) )
                        .findFirst().orElseThrow( () -> new IOException( "Missing DwarFS extractor" ) ).toString() );
                }
            }
            else
            {
                if ( !tool.toFile().setExecutable( true, true ) ) { throw new IOException( "Cannot execute DwarFS build tool" ); }
                command.add( tool.toString() );
                command.add( "--tool=dwarfsextract" );
            }
        }
        final Path tar = work.resolve( "distribution.tar" );
        final Path errors = work.resolve( "dwarfs.log" );
        command.addAll( List.of( "-i", archive.toString(), "-f", "pax", "-o", tar.toString(),
            "--skip-devices", "--skip-specials" ) );
        await( new ProcessBuilder( command ).redirectOutput( ProcessBuilder.Redirect.DISCARD )
            .redirectError( errors.toFile() ).start(), errors );
        final Map<String, Path> files = new LinkedHashMap<>();
        final Map<String, String> links = new HashMap<>();
        try (var input = new TarArchiveInputStream( new BufferedInputStream( Files.newInputStream( tar ) ) ))
        {
            TarArchiveEntry member;
            while ( ( member = input.getNextEntry() ) != null )
            {
                if ( member.isDirectory() ) { continue; }
                final String name = safeName( member.getName() );
                if ( files.containsKey( name ) || links.containsKey( name ) ) { throw new IOException( "Duplicate TAR entry" ); }
                if ( member.isSymbolicLink() ) { links.put( name, member.getLinkName() ); }
                else if ( member.isLink() )
                {
                    final Path parent = Path.of( name ).getParent();
                    final Path link = Path.of( safeName( member.getLinkName() ) );
                    links.put( name, parent == null ? link.toString() : parent.relativize( link ).toString().replace( '\\', '/' ) );
                }
                else if ( member.isFile() )
                {
                    final Path file = work.resolve( "dwarfs-file-" + files.size() );
                    Files.copy( input, file );
                    files.put( name, file );
                }
                else { throw new IOException( "Unsupported TAR entry" ); }
            }
        }
        materialize( target, files, links );
    }

    private static void unpackSevenZip( final Path archive, final Path target, final Path work, final String sevenZip )
        throws Exception
    {
        final Path listing = work.resolve( "listing.txt" );
        final Path errors = work.resolve( "7zip.log" );
        final Process list = new ProcessBuilder( sevenZip, "l", "-slt", "-ba", "-sccUTF-8", archive.toString() )
            .redirectOutput( listing.toFile() ).redirectError( errors.toFile() ).start();
        await( list, errors );
        final List<Map<String, String>> entries = new ArrayList<>();
        Map<String, String> entry = new LinkedHashMap<>();
        for ( String line : Files.readAllLines( listing, StandardCharsets.UTF_8 ) )
        {
            if ( line.isBlank() )
            {
                if ( !entry.isEmpty() ) { entries.add( entry ); entry = new LinkedHashMap<>(); }
            }
            else
            {
                final int split = line.indexOf( " = " );
                if ( split > 0 ) { entry.put( line.substring( 0, split ), line.substring( split + 3 ) ); }
            }
        }
        if ( !entry.isEmpty() ) { entries.add( entry ); }
        final Map<String, Path> files = new LinkedHashMap<>();
        final Map<String, String> links = new HashMap<>();
        final Process extract = new ProcessBuilder( sevenZip, "x", "-so", "-y", "-bd", archive.toString() )
            .redirectError( errors.toFile() ).start();
        final var worker = Executors.newSingleThreadExecutor();
        try
        {
            extract.getOutputStream().close();
            // Read archive entries as bytes: no build-host symlink privileges or foreign executables are needed.
            final var reading = worker.submit( () -> {
                try (InputStream input = new BufferedInputStream( extract.getInputStream() ))
                {
                    for ( Map<String, String> member : entries )
                    {
                        final String mode = member.getOrDefault( "Mode", "" );
                        if ( "+".equals( member.get( "Folder" ) ) || mode.startsWith( "d" ) ||
                            member.getOrDefault( "Attributes", "" ).contains( "D" ) ) { continue; }
                        final String name = safeName( member.get( "Path" ).replace( File.separatorChar, '/' ) );
                        final long size = Long.parseLong( member.get( "Size" ) );
                        if ( files.containsKey( name ) || links.containsKey( name ) ) { throw new IOException( "Duplicate archive path" ); }
                        if ( mode.startsWith( "l" ) )
                        {
                            if ( size < 1 || size > 4096 ) { throw new IOException( "Invalid archive link" ); }
                            final byte[] bytes = input.readNBytes( (int) size );
                            if ( bytes.length != size ) { throw new IOException( "Truncated archive link" ); }
                            links.put( name, new String( bytes, StandardCharsets.UTF_8 ) );
                        }
                        else
                        {
                            final Path file = work.resolve( "file-" + files.size() );
                            try (OutputStream output = Files.newOutputStream( file )) { copyExactly( input, output, size ); }
                            files.put( name, file );
                        }
                    }
                    if ( input.read() != -1 ) { throw new IOException( "Unexpected archive data" ); }
                    return null;
                }
            } );
            reading.get( 180, TimeUnit.SECONDS );
            await( extract, errors );
        }
        finally
        {
            stop( extract );
            worker.shutdownNow();
            worker.awaitTermination( 10, TimeUnit.SECONDS );
        }
        materialize( target, files, links );
    }

    private static void materialize( final Path target, final Map<String, Path> files, final Map<String, String> links )
        throws IOException
    {
        final Set<String> names = new HashSet<>( files.keySet() );
        names.addAll( links.keySet() );
        for ( String name : names )
        {
            final String resolved = resolveName( name, links, new HashSet<>() );
            if ( files.containsKey( resolved ) )
            {
                copy( files.get( resolved ), target.resolve( name ) );
            }
            else
            {
                boolean found = false;
                for ( String child : names )
                {
                    if ( child.startsWith( resolved + "/" ) )
                    {
                        final String source = resolveName( child, links, new HashSet<>() );
                        if ( files.containsKey( source ) )
                        {
                            copy( files.get( source ), target.resolve( name + child.substring( resolved.length() ) ) );
                            found = true;
                        }
                    }
                }
                if ( !found ) { throw new IOException( "Unresolved archive link: " + name ); }
            }
        }
    }

    private static void copy( final Path source, final Path file ) throws IOException
    {
        Files.createDirectories( file.getParent() );
        Files.copy( source, file, StandardCopyOption.REPLACE_EXISTING );
    }

    static Path resolve( final String name, final Map<String, Path> files, final Map<String, String> links,
                         final Set<String> visited ) throws IOException
    {
        final Path file = files.get( resolveName( name, links, visited ) );
        if ( file == null ) { throw new IOException( "Unresolved archive link: " + name ); }
        return file;
    }

    private static String resolveName( final String name, final Map<String, String> links, final Set<String> visited )
        throws IOException
    {
        String prefix = name;
        while ( !links.containsKey( prefix ) )
        {
            final int slash = prefix.lastIndexOf( '/' );
            if ( slash < 0 ) { return name; }
            prefix = prefix.substring( 0, slash );
        }
        final String link = links.get( prefix );
        if ( !visited.add( prefix ) || link.startsWith( "/" ) )
        {
            throw new IOException( "Unresolved or cyclic archive link: " + name );
        }
        final Path parent = Path.of( prefix ).getParent();
        final String resolved = safeName( ( parent == null ? Path.of( link ) : parent.resolve( link ) )
            .normalize().toString().replace( '\\', '/' ) + name.substring( prefix.length() ) );
        return resolveName( resolved, links, visited );
    }

    static void writeIndex( final Path target, final String command ) throws IOException
    {
        if ( !Files.isRegularFile( target.resolve( safeName( command ) ) ) ) { throw new IOException( "Missing ImageMagick executable" ); }
        final StringBuilder index = new StringBuilder( command ).append( '\n' );
        try (var paths = Files.walk( target ))
        {
            for ( Path file : paths.filter( Files::isRegularFile ).sorted().toList() )
            {
                final String name = safeName( target.relativize( file ).toString().replace( '\\', '/' ) );
                final byte[] header;
                try (var input = Files.newInputStream( file )) { header = input.readNBytes( 4 ); }
                final boolean executable = name.equals( command ) || header.length >= 2 && header[0] == '#' && header[1] == '!' ||
                    header.length == 4 && ( header[0] == 0x7f && header[1] == 'E' && header[2] == 'L' && header[3] == 'F' ||
                        ( header[0] & 0xff ) == 0xcf && ( header[1] & 0xff ) == 0xfa );
                index.append( executable ? 'x' : '-' ).append( '\t' ).append( name ).append( '\n' );
            }
        }
        Files.writeString( target.resolve( "files.txt" ), index, StandardCharsets.UTF_8 );
    }

    static void removeUpdateHooks( final Path target ) throws IOException
    {
        try (var paths = Files.walk( target ))
        {
            for ( Path path : paths.filter( Files::isRegularFile )
                .filter( path -> path.getFileName().toString().endsWith( "self-updater.hook" ) ).toList() ) { Files.delete( path ); }
        }
    }

    static String safeName( final String name ) throws IOException
    {
        if ( name == null || name.isBlank() || name.startsWith( "/" ) || name.contains( "\\" ) || name.contains( ":" ) ||
            name.chars().anyMatch( c -> c < 32 ) ) { throw new IOException( "Invalid archive path" ); }
        final String normalized = Path.of( name ).normalize().toString().replace( '\\', '/' );
        if ( normalized.isEmpty() || normalized.equals( ".." ) || normalized.startsWith( "../" ) )
        {
            throw new IOException( "Archive path escapes distribution" );
        }
        return normalized;
    }

    private static void copyExactly( final InputStream input, final OutputStream output, final long length ) throws IOException
    {
        if ( length < 0 ) { throw new IOException( "Invalid archive entry length" ); }
        long remaining = length;
        final byte[] buffer = new byte[65536];
        while ( remaining > 0 )
        {
            final int count = input.read( buffer, 0, (int) Math.min( buffer.length, remaining ) );
            if ( count < 0 ) { throw new IOException( "Truncated archive data" ); }
            output.write( buffer, 0, count );
            remaining -= count;
        }
    }

    private static void await( final Process process, final Path errors ) throws Exception
    {
        try
        {
            process.getOutputStream().close();
            if ( !process.waitFor( 180, TimeUnit.SECONDS ) || process.exitValue() != 0 )
            {
                throw new IOException( "ImageMagick build extraction failed: " + Files.readString( errors ) );
            }
        }
        finally { stop( process ); }
    }

    private static void stop( final Process process ) throws InterruptedException
    {
        if ( process.isAlive() )
        {
            process.descendants().forEach( ProcessHandle::destroyForcibly );
            process.destroyForcibly().waitFor();
        }
    }

    private static void delete( final Path directory ) throws IOException
    {
        try (var paths = Files.walk( directory ))
        {
            for ( Path path : paths.sorted( Comparator.reverseOrder() ).toList() ) { Files.deleteIfExists( path ); }
        }
    }
}
