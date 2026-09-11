package com.enonic.xp.core.impl.image;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/** Loads the platform distribution from this bundle; never downloads code at runtime. */
final class EmbeddedImageMagick
{
    private static Path executable;

    static synchronized Path executable()
        throws IOException
    {
        if ( executable != null )
        {
            return executable;
        }
        final String platform = platform( System.getProperty( "os.name" ), System.getProperty( "os.arch" ) );
        final boolean windows = platform.startsWith( "windows-" );
        final String suffix = windows ? ".zip" : ".AppImage";
        try (InputStream resource = EmbeddedImageMagick.class.getResourceAsStream( "/native/imagemagick/" + platform + suffix ))
        {
            if ( resource == null )
            {
                throw new IOException( "Embedded ImageMagick is unavailable for " + platform );
            }
            // A private, unique directory prevents another XP process from replacing this executable.
            final Path directory = Files.createTempDirectory( "xp-imagemagick-" );
            try
            {
                final Path archive = directory.resolve( "distribution" + suffix );
                Files.copy( resource, archive );
                if ( windows )
                {
                    extractWindows( archive, directory );
                }
                else
                {
                    extractLinux( archive, directory );
                }
                Files.delete( archive );
                final Path candidate = directory.resolve( windows ? "magick.exe" : "squashfs-root/AppRun" );
                if ( !Files.isRegularFile( candidate ) || !Files.isExecutable( candidate ) )
                {
                    throw new IOException( "Cannot execute embedded ImageMagick in " + directory );
                }
                Runtime.getRuntime().addShutdownHook( new Thread( () -> delete( directory ), "image-encoder-cleanup" ) );
                executable = candidate;
                return executable;
            }
            catch ( IOException | RuntimeException e )
            {
                delete( directory );
                throw e;
            }
        }
    }

    static String platform( final String os, final String arch )
    {
        final String system = os.toLowerCase( Locale.ROOT );
        final String machine = arch.toLowerCase( Locale.ROOT );
        final String normalizedOs = system.startsWith( "windows" ) ? "windows" : system.startsWith( "linux" ) ? "linux" :
            system.startsWith( "mac" ) ? "osx" : system;
        final String normalizedArch = switch ( machine )
        {
            case "amd64", "x86_64" -> "x86_64";
            case "arm64", "aarch64" -> "aarch64";
            default -> machine;
        };
        return normalizedOs + "-" + normalizedArch;
    }

    static void extractWindows( final Path archive, final Path directory )
        throws IOException
    {
        try (ZipInputStream zip = new ZipInputStream( Files.newInputStream( archive ) ))
        {
            ZipEntry entry;
            final byte[] buffer = new byte[8192];
            while ( ( entry = zip.getNextEntry() ) != null )
            {
                final Path path = directory.resolve( entry.getName() ).normalize();
                if ( !path.startsWith( directory ) )
                {
                    throw new IOException( "Invalid embedded ImageMagick archive entry" );
                }
                if ( entry.isDirectory() )
                {
                    Files.createDirectories( path );
                }
                else
                {
                    Files.createDirectories( path.getParent() );
                    try (var output = Files.newOutputStream( path ))
                    {
                        int read;
                        while ( ( read = zip.read( buffer ) ) != -1 )
                        {
                            output.write( buffer, 0, read );
                        }
                    }
                }
            }
        }
    }

    private static void extractLinux( final Path archive, final Path directory )
        throws IOException
    {
        if ( !archive.toFile().setExecutable( true, true ) )
        {
            throw new IOException( "Cannot execute embedded ImageMagick extractor" );
        }
        // --appimage-extract works without FUSE, root, or an ImageMagick installation.
        final Process process = new ProcessBuilder( archive.toString(), "--appimage-extract" )
            .directory( directory.toFile() ).redirectOutput( ProcessBuilder.Redirect.DISCARD )
            .redirectError( ProcessBuilder.Redirect.DISCARD ).start();
        try
        {
            process.getOutputStream().close();
            if ( !process.waitFor( 120, TimeUnit.SECONDS ) || process.exitValue() != 0 )
            {
                throw new IOException( "Embedded ImageMagick extraction failed" );
            }
        }
        catch ( InterruptedException e )
        {
            Thread.currentThread().interrupt();
            throw new IOException( "Embedded ImageMagick extraction interrupted", e );
        }
        finally
        {
            ImageMagickEncoder.stop( process );
        }
    }

    private static void delete( final Path directory )
    {
        try (var paths = Files.walk( directory ))
        {
            for ( final Path path : paths.sorted( Comparator.reverseOrder() ).toList() )
            {
                Files.deleteIfExists( path );
            }
        }
        catch ( IOException ignored )
        {
            // Windows may keep native libraries locked until the process exits.
        }
    }
}
