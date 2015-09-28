package com.enonic.xp.toolbox.app;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Charsets;

public class InitAppCommandTest
{

    private static File targetDirectory;

    @ClassRule
    public static TemporaryFolder temporaryFolder = new TemporaryFolder();

    @BeforeClass
    public static void before()
        throws IOException
    {
        targetDirectory = temporaryFolder.newFolder();
    }

    @Test
    public void initApp()
        throws IOException, URISyntaxException
    {
        final InitAppCommand command = new InitAppCommand();
        command.name = "com.enonic.xp.toolbox.app.initCommandTest";
        command.repository = getClass().getResource( "/starter-empty/.git-directory" ).toURI().toString();
        command.destination = targetDirectory.getAbsolutePath();
        command.version = "1.0.1";

        command.run();

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
        Assert.assertTrue( gradlePropertiesContent.contains( "displayName = InitCommandTest App" ) );
        Assert.assertTrue( gradlePropertiesContent.contains( "xpVersion = 6.1.0-SNAPSHOT" ) );
    }
}
