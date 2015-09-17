package com.enonic.xp.toolbox.app;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import io.airlift.airline.Command;
import io.airlift.airline.Option;

import com.enonic.xp.toolbox.ToolCommand;

@Command(name = "init-app", description = "Initiates an Enonic XP application.")
public final class InitAppCommand
    extends ToolCommand
{
    @Option(name = {"-n", "--name"}, description = "Application name.", required = true)
    public String name;

    @Option(name = {"-v", "--version"}, description = "Version number.")
    public String version = "1.0.0-SNAPSHOT";

    @Option(name = {"-d", "--destination"}, description = "Project path.")
    public String destination = ".";

    @Override
    protected void execute()
        throws Exception
    {
        try
        {
            initApplication();
            updateFile( Paths.get( destination, "build.gradle" ), "##NAME##", name );
            updateFile( Paths.get( destination, "build.gradle" ), "##VERSION##", version );
        }
        catch ( Exception e )
        {
            System.err.println( e.getMessage() );
            e.printStackTrace();
        }
    }

    private void initApplication()
        throws URISyntaxException, IOException
    {
        final Path sourceDirectory = getInitAppResourcesPath();
        final Path targetDirectory = Paths.get( destination );

        Files.walkFileTree( sourceDirectory, new SimpleFileVisitor<Path>()
        {
            @Override
            public FileVisitResult preVisitDirectory( Path dir, BasicFileAttributes attrs )
                throws IOException
            {
                Path target = targetDirectory.resolve( sourceDirectory.relativize( dir ).toString() );
                try
                {
                    Files.copy( dir, target );
                }
                catch ( FileAlreadyExistsException e )
                {
                    if ( !Files.isDirectory( target ) )
                    {
                        Files.copy( dir, target, StandardCopyOption.REPLACE_EXISTING );
                    }
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile( Path file, BasicFileAttributes attrs )
                throws IOException
            {
                if ( !".gitkeep".equals( file.getFileName().toString() ) )
                {
                    final Path target = targetDirectory.resolve( sourceDirectory.relativize( file ).toString() );
                    Files.copy( file, target, StandardCopyOption.REPLACE_EXISTING );
                }
                return FileVisitResult.CONTINUE;
            }
        } );

        targetDirectory.resolve( "gradlew" ).toFile().setExecutable( true );
    }

    private Path getInitAppResourcesPath()
        throws URISyntaxException, IOException
    {
        final String uri = getClass().getResource( "/init-app" ).toURI().toString();
        if ( uri.startsWith( "jar:" ) )
        {
            final String[] uriSubStrings = uri.split( "!" );
            Map<String, String> env = new HashMap<>();
            env.put( "create", "true" );
            FileSystem fs = FileSystems.newFileSystem( new URI( uriSubStrings[0] ), env );
            return fs.getPath( uriSubStrings[1] );
        }
        else
        {
            return Paths.get( new URI( uri ) );
        }
    }

    private void updateFile( Path filePath, String regex, String replacement )
        throws IOException
    {
        Charset charset = StandardCharsets.UTF_8;
        String content = new String( Files.readAllBytes( filePath ), charset );
        content = content.replaceAll( regex, replacement );
        Files.write( filePath, content.getBytes( charset ) );
    }
}
