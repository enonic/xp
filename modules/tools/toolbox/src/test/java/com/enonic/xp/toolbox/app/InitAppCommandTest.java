package com.enonic.xp.toolbox.app;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Charsets;

public class InitAppCommandTest
{

    @ClassRule
    public static TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File targetDirectory;

    @Before
    public void before()
        throws IOException
    {
        targetDirectory = temporaryFolder.newFolder();
    }

    @Test
    public void initApp()
        throws IOException, URISyntaxException
    {
        createInitAppCommand().run();

        Assert.assertEquals( 6, targetDirectory.list().length );
        Assert.assertTrue( !Files.exists( Paths.get( targetDirectory.getPath(), ".git" ) ) );
        Assert.assertTrue( Files.exists( Paths.get( targetDirectory.getPath(), "src", "main", "resources", "site", "assets" ) ) );
        Assert.assertTrue( !Files.exists( Paths.get( targetDirectory.getPath(), "src", "main", "resources", "assets", ".gitkeep" ) ) );

        final String gradlePropertiesContent =
            com.google.common.io.Files.asCharSource( new File( targetDirectory, "gradle.properties" ), Charsets.UTF_8 ).read();

        Assert.assertTrue( gradlePropertiesContent.contains( "group = com.enonic.xp.toolbox.app" ) );
        Assert.assertTrue( gradlePropertiesContent.contains( "version = 1.0.1" ) );
        Assert.assertTrue( gradlePropertiesContent.contains( "projectName = initCommandTest" ) );
        Assert.assertTrue( gradlePropertiesContent.contains( "appName = com.enonic.xp.toolbox.app.initCommandTest" ) );
        Assert.assertTrue( gradlePropertiesContent.contains( "displayName = InitCommandTest" ) );
        Assert.assertTrue( gradlePropertiesContent.contains( "xpVersion = 6.1.0-SNAPSHOT" ) );
    }

    @Test
    public void cloneAndCheckout()
        throws URISyntaxException, IOException
    {
        final InitAppCommand command = createInitAppCommand();
        command.checkout = "7440461f1651f64417dab7179a8c1ec91922d850";
        command.run();

        Assert.assertEquals( 6, targetDirectory.list().length );
        final String gradlePropertiesContent =
            com.google.common.io.Files.asCharSource( new File( targetDirectory, "gradle.properties" ), Charsets.UTF_8 ).read();
        Assert.assertTrue( gradlePropertiesContent.isEmpty() );
    }

    private InitAppCommand createInitAppCommand()
        throws URISyntaxException
    {
        final InitAppCommand command = new InitAppCommand();
        command.name = "com.enonic.xp.toolbox.app.initCommandTest";
        command.repository = getClass().getResource( "/starter-empty/.git-directory" ).toURI().toString();
        command.destination = targetDirectory.getAbsolutePath();
        command.version = "1.0.1";

        return command;
    }
}
