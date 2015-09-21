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
    public void minimal()
        throws IOException, URISyntaxException
    {
        final InitAppCommand command = new InitAppCommand();
        command.name = "myapp";
        command.repository = getClass().getResource( "/starter-empty/.git-directory" ).toURI().toString();
        command.destination = targetDirectory.getAbsolutePath();

        command.run();

        Assert.assertEquals( 6, targetDirectory.list().length );
        Assert.assertTrue( !Files.exists( Paths.get( targetDirectory.getPath(), ".git" ) ) );
        Assert.assertTrue( Files.exists( Paths.get( targetDirectory.getPath(), "src", "main", "resources", "site", "assets" ) ) );
        Assert.assertTrue( !Files.exists( Paths.get( targetDirectory.getPath(), "src", "main", "resources", "assets", ".gitkeep" ) ) );
    }
}
