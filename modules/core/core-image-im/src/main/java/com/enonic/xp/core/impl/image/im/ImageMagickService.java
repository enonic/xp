package com.enonic.xp.core.impl.image.im;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import com.enonic.xp.core.internal.image.ImageMagick;

/** Owns the native installation for one OSGi component lifetime. */
@NullMarked
@Component(service = ImageMagick.class)
public final class ImageMagickService implements ImageMagick
{
    private final Path storage;
    private final String platform;
    private final Function<String, @Nullable InputStream> resources;
    private @Nullable Path directory;
    private @Nullable Path executable;
    private Map<String, String> environment = Map.of();
    private int handles;
    private boolean active = true;

    /**
     * Creates a service using the bundle's private data area.
     *
     * @param context the owning bundle context
     * @throws IllegalStateException if the framework has no bundle data area
     */
    @Activate
    public ImageMagickService( final BundleContext context )
    {
        final var data = context.getDataFile( "imagemagick" );
        if ( data == null )
        {
            throw new IllegalStateException( "ImageMagick requires a bundle data area" );
        }
        storage = data.toPath();
        platform = platform( System.getProperty( "os.name" ), System.getProperty( "os.arch" ) );
        resources = ImageMagickService.class::getResourceAsStream;
    }

    ImageMagickService( final Path storage, final String platform,
                       final Function<String, @Nullable InputStream> resources )
    {
        this.storage = storage;
        this.platform = platform;
        this.resources = resources;
    }

    /**
     * Acquires a handle retaining this platform's installation.
     *
     * @return an installation handle
     * @throws IOException if the service has stopped or the installation cannot be prepared
     */
    @Override
    public synchronized Installation acquire() throws IOException
    {
        if ( !active )
        {
            throw new IOException( "ImageMagick service has stopped" );
        }
        if ( executable == null )
        {
            prepare();
        }
        handles++;
        return new Handle( executable, environment );
    }

    /**
     * Stops accepting new handles. Existing handles retain their installation until closed.
     *
     * @throws IOException if an unused installation cannot be removed
     */
    @Deactivate
    public synchronized void deactivate() throws IOException
    {
        active = false;
        if ( handles == 0 )
        {
            cleanup();
        }
    }

    private void prepare() throws IOException
    {
        final String root = "/native/imagemagick/" + platform + "/";
        try (InputStream index = resources.apply( root + "files.txt" ))
        {
            if ( index == null )
            {
                throw new IOException( "Embedded ImageMagick is unavailable for " + platform );
            }
            Files.createDirectories( storage );
            final Path target = Files.createTempDirectory( storage, "installation-" ).toAbsolutePath();
            try
            {
                final var reader = new BufferedReader( new InputStreamReader( index, StandardCharsets.UTF_8 ) );
                final String command = reader.readLine();
                if ( command == null ) { throw new IOException( "Missing ImageMagick executable" ); }
                final Path candidate = safePath( target, command );
                final var names = new HashSet<String>();
                String line;
                while ( ( line = reader.readLine() ) != null )
                {
                    if ( line.length() < 3 || line.charAt( 1 ) != '\t' ||
                        ( line.charAt( 0 ) != 'x' && line.charAt( 0 ) != '-' ) )
                    {
                        throw new IOException( "Invalid ImageMagick file index" );
                    }
                    final String name = line.substring( 2 );
                    final Path file = safePath( target, name );
                    if ( !names.add( name ) ) { throw new IOException( "Duplicate ImageMagick file" ); }
                    try (InputStream input = resources.apply( root + name ))
                    {
                        if ( input == null ) { throw new IOException( "Missing ImageMagick resource: " + name ); }
                        Files.createDirectories( file.getParent() );
                        Files.copy( input, file );
                    }
                    if ( line.charAt( 0 ) == 'x' && !platform.startsWith( "windows-" ) &&
                        !file.toFile().setExecutable( true, true ) )
                    {
                        throw new IOException( "Cannot make ImageMagick resource executable" );
                    }
                }
                if ( !Files.isRegularFile( candidate ) || !Files.isExecutable( candidate ) )
                {
                    throw new IOException( "Cannot execute embedded ImageMagick" );
                }
                environment = platform.startsWith( "osx-" ) ? Map.of(
                    "MAGICK_HOME", target.toString(),
                    "MAGICK_CONFIGURE_PATH", target.resolve( "etc/ImageMagick-7" ).toString(),
                    "LIBHEIF_PLUGIN_PATH", target.resolve( "lib/libheif" ).toString() ) : Map.of();
                directory = target;
                executable = candidate;
            }
            catch ( IOException | RuntimeException | Error e )
            {
                try { delete( target ); }
                catch ( IOException cleanup ) { e.addSuppressed( cleanup ); }
                throw e;
            }
        }
    }

    private static Path safePath( final Path directory, final String name ) throws IOException
    {
        if ( name.isBlank() || name.startsWith( "/" ) || name.contains( "\\" ) || name.contains( ":" ) ||
            name.chars().anyMatch( c -> c < 32 ) )
        {
            throw new IOException( "Invalid ImageMagick resource path" );
        }
        final Path path = directory.resolve( name ).normalize();
        if ( !path.startsWith( directory ) || path.equals( directory ) )
        {
            throw new IOException( "Invalid ImageMagick resource path" );
        }
        return path;
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

    private void cleanup() throws IOException
    {
        if ( directory != null )
        {
            delete( directory );
            directory = null;
            executable = null;
        }
    }

    private static void delete( final Path directory ) throws IOException
    {
        try (var paths = Files.walk( directory ))
        {
            for ( Path path : paths.sorted( Comparator.reverseOrder() ).toList() ) { Files.deleteIfExists( path ); }
        }
    }

    private final class Handle implements Installation
    {
        private final Path command;
        private final Map<String, String> settings;
        private boolean closed;

        private Handle( final Path command, final Map<String, String> settings )
        {
            this.command = command;
            this.settings = settings;
        }

        @Override
        public Path executable() { return command; }

        @Override
        public Map<String, String> environment() { return settings; }

        @Override
        public void close() throws IOException
        {
            synchronized ( ImageMagickService.this )
            {
                if ( !closed )
                {
                    closed = true;
                    handles--;
                    if ( !active && handles == 0 ) { cleanup(); }
                }
            }
        }
    }
}
