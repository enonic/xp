package com.enonic.xp.toolbox.app;


import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;

import io.airlift.airline.Command;
import io.airlift.airline.Option;

import com.enonic.xp.toolbox.ToolCommand;

@Command(name = "init-project", description = "Initiates an Enonic XP application project.")
public final class InitAppCommand
    extends ToolCommand
{
    private final static Logger LOGGER = LoggerFactory.getLogger( InitAppCommand.class );

    private static final String GITHUB_URL = "https://github.com/";

    private static final String ENONIC_REPOSITORY_PREFIX = "enonic/";

    private static final String GIT_REPOSITORY_SUFFIX = ".git";

    @Option(name = "-a", description = "Authentication token for basic authentication (user:password).")
    public String authentication;

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
        processGradleProperties();
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
        LOGGER.info( "Retrieving Git repository from \"" + gitRepositoryUri + "\" ..." );

        // Creates the destination directory if it does not exist
        File destinationDirectory = new File( destination );
        File temporaryDirectory = new File( destinationDirectory, ".InitAppTemporaryDirectory" );
        temporaryDirectory.mkdirs();

        // Clones the Git repository
        final CloneCommand cloneCommand = Git.cloneRepository().
            setURI( gitRepositoryUri ).
            setDirectory( temporaryDirectory );
        if ( authentication != null )
        {
            final String[] authentificationValues = authentication.split( ":" );
            cloneCommand.setCredentialsProvider(
                new UsernamePasswordCredentialsProvider( authentificationValues[0], authentificationValues[1] ) );
        }
        Git clone = cloneCommand.call();
        clone.getRepository().close();

        // Removes the Git related content
        removeGitRelatedContent( temporaryDirectory );

        //Copies the content from the temporary folder and remove it
        FileUtils.copyDirectory( temporaryDirectory, destinationDirectory );
        FileUtils.deleteDirectory( temporaryDirectory );

        LOGGER.info( "Git repository retrieved." );
    }

    private void removeGitRelatedContent( File directory )
        throws IOException
    {
        //Removes the .git directory and README.md file
        FileUtils.deleteDirectory( new File( directory, ".git" ) );
        FileUtils.deleteQuietly( new File( directory, "README.md" ) );

        //Remove the .gitkeep and .gitignore files
        Files.walkFileTree( directory.toPath(), new SimpleFileVisitor<Path>()
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

    private void processGradleProperties()
        throws IOException
    {
        LOGGER.info( "Adapting Gradle properties file ..." );

        //Creates the Gradle Properties file if it does not exist
        final File gradlePropertiesFile = new File( destination, "gradle.properties" );
        if ( !gradlePropertiesFile.exists() )
        {
            gradlePropertiesFile.createNewFile();
        }

        //Process the content of the Gradle Properties file
        final List<String> originalGradlePropertiesContent = com.google.common.io.Files.readLines( gradlePropertiesFile, Charsets.UTF_8 );
        final GradlePropertiesProcessor gradlePropertiesProcessor = new GradlePropertiesProcessor( name, version );
        final List<String> processedGradlePropertiesContent = gradlePropertiesProcessor.process( originalGradlePropertiesContent );

        //Write the processed content into the  Gradle Properties file
        com.google.common.io.Files.asCharSink( gradlePropertiesFile, Charsets.UTF_8 ).writeLines( processedGradlePropertiesContent );

        LOGGER.info( "Gradle properties file adapted." );
    }
}
