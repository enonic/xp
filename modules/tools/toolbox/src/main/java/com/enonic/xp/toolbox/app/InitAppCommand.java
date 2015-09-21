package com.enonic.xp.toolbox.app;


import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import com.google.common.base.Charsets;

import io.airlift.airline.Command;
import io.airlift.airline.Option;

import com.enonic.xp.toolbox.ToolCommand;

@Command(name = "init-app", description = "Initiates an Enonic XP application.")
public final class InitAppCommand
    extends ToolCommand
{
    private static final String GITHUB_URL = "https://github.com/";

    private static final String ENONIC_REPOSITORY_PREFIX = "enonic/";

    private static final String GIT_REPOSITORY_SUFFIX = "/.git";

    @Option(name = {"-d", "--destination"}, description = "Destination path.")
    public String destination = ".";

    @Option(name = {"-n", "--name"}, description = "Application name.", required = true)
    public String name;

    @Option(name = {"-r", "--repository"}, description = "Git repository.", required = true)
    public String repository;

    @Option(name = {"-v", "--version"}, description = "Version number.")
    public String version = "1.0.0-SNAPSHOT";

    @Override
    protected void execute()
        throws Exception
    {
        String gitRepositoryUri = resolveGitRepositoryUri();
        cloneGitRepository( gitRepositoryUri );
        adaptGradleProperties();
    }

    private String resolveGitRepositoryUri()
    {
        if ( repository.contains( ":/" ) )
        {
            return repository;
        }
        if ( repository.contains( "/" ) )
        {
            return GITHUB_URL + repository + GIT_REPOSITORY_SUFFIX;
        }
        return GITHUB_URL + ENONIC_REPOSITORY_PREFIX + repository + GIT_REPOSITORY_SUFFIX;
    }

    private void cloneGitRepository( final String gitRepositoryUri )
        throws GitAPIException, IOException
    {
        // Creates the destination directory if it does not exist
        File destinationDirectory = new File( destination );
        if ( !destinationDirectory.exists() )
        {
            destinationDirectory.mkdirs();
        }

        // Clones the Git repository
        Git.cloneRepository().
            setURI( gitRepositoryUri ).
            setDirectory( destinationDirectory ).
            call();

        //Removes the .git folder
        FileUtils.deleteDirectory( new File( destinationDirectory, ".git" ) );

        //Remove the .gitkeep and .gitignore files
        Files.walkFileTree( destinationDirectory.toPath(), new SimpleFileVisitor<Path>()
        {
            @Override
            public FileVisitResult visitFile( final Path file, final BasicFileAttributes attrs )
                throws IOException
            {
                final String fileName = file.getFileName().toString();
                if ( ".gitkeep".equals( fileName ) || ".gitignore".equals( fileName ) )
                {
                    Files.delete( file );
                }
                return FileVisitResult.CONTINUE;
            }
        } );
    }

    private void adaptGradleProperties()
        throws IOException
    {
        //Creates the Gradle Properties file if it does not exist
        final File gradlePropertiesFile = new File( destination, "gradle.properties" );
        if ( !gradlePropertiesFile.exists() )
        {
            gradlePropertiesFile.createNewFile();
        }

        final String adaptedGradlePropertiesContent =
            com.google.common.io.Files.readLines( gradlePropertiesFile, Charsets.UTF_8, new GradlePropertiesProcessor( name, version ) );

        com.google.common.io.Files.asCharSink( gradlePropertiesFile, Charsets.UTF_8 ).write( adaptedGradlePropertiesContent );
    }
}
