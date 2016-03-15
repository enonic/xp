package com.enonic.xp.toolbox.app;


import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
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

    @Option(name = {"-c", "--checkout"}, description = "Branch or commit to checkout.")
    public String checkout;

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

        try
        {
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
            Git git = cloneCommand.call();

            // Checks out the specified branch or commit if necessary
            if ( checkout != null )
            {
                git.checkout().setName( checkout ).call();
            }

            //Closes the repository
            git.getRepository().close();

            // Removes Git related content
            removeFixGitContent( temporaryDirectory );

            // Copies the content from the temporary folder
            final CopyFileVisitor copyFileVisitor = new CopyFileVisitor( temporaryDirectory.toPath(), destinationDirectory.toPath() );
            Files.walkFileTree( temporaryDirectory.toPath(), copyFileVisitor );
        }
        finally
        {
            // Removes the temporary folder
            FileUtils.deleteDirectory( temporaryDirectory );
        }

        LOGGER.info( "Git repository retrieved." );
    }

    private void removeFixGitContent( File directory )
        throws IOException
    {
        // Removes the .git directory and README.md file
        FileUtils.deleteDirectory( new File( directory, ".git" ) );
        FileUtils.deleteQuietly( new File( directory, "README.md" ) );
    }

    private void processGradleProperties()
        throws IOException
    {
        final File gradlePropertiesFile = new File( destination, "gradle.properties" );
        if ( gradlePropertiesFile.exists() )
        {
            LOGGER.info( "Adapting Gradle properties file ..." );
            // Process the content of the Gradle Properties file
            final List<String> originalGradlePropertiesContent =
                com.google.common.io.Files.readLines( gradlePropertiesFile, Charsets.UTF_8 );
            final GradlePropertiesProcessor gradlePropertiesProcessor = new GradlePropertiesProcessor( name, version );
            final List<String> processedGradlePropertiesContent = gradlePropertiesProcessor.process( originalGradlePropertiesContent );

            // Write the processed content into the  Gradle Properties file
            com.google.common.io.Files.asCharSink( gradlePropertiesFile, Charsets.UTF_8 ).writeLines( processedGradlePropertiesContent );

            LOGGER.info( "Gradle properties file adapted." );
        }
    }

    private class CopyFileVisitor
        extends SimpleFileVisitor<Path>
    {
        final Path sourcePath;

        final Path targetPath;

        private boolean rootFile = true;

        private CopyFileVisitor( Path sourcePath, Path targetPath )
        {
            this.sourcePath = sourcePath;
            this.targetPath = targetPath;
        }

        @Override
        public FileVisitResult visitFile( final Path sourceFilePath, final BasicFileAttributes attrs )
            throws IOException
        {
            final String fileName = sourceFilePath.getFileName().toString();
            final Path sourceFileSubPath = sourcePath.relativize( sourceFilePath );
            final Path targetFilePath = Paths.get( targetPath.toString(), sourceFileSubPath.toString() );
            Files.move( sourceFilePath, targetFilePath, StandardCopyOption.REPLACE_EXISTING, LinkOption.NOFOLLOW_LINKS );
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult preVisitDirectory( final Path sourceFilePath, final BasicFileAttributes attrs )
            throws IOException
        {

            if ( rootFile )
            {
                rootFile = false;
            }
            else
            {
                final Path sourceFileSubPath = sourcePath.relativize( sourceFilePath );
                final Path targetFilePath = Paths.get( targetPath.toString(), sourceFileSubPath.toString() );

                if ( !Files.exists( targetFilePath ) )
                {
                    Files.copy( sourceFilePath, targetFilePath, StandardCopyOption.COPY_ATTRIBUTES, LinkOption.NOFOLLOW_LINKS );
                }
            }
            return FileVisitResult.CONTINUE;
        }
    }
}
