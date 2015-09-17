package com.enonic.xp.toolbox.app;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class InitAppCommandTest
{
    @Rule
    public TemporaryFolder targetRoot = new TemporaryFolder();

    @Test
    public void test()
        throws IOException
    {
        final InitAppCommand command = new InitAppCommand();
        command.name = "myapp";
        command.destination = targetRoot.getRoot().getAbsolutePath();
        command.version = "X.Y.Z";

        command.run();

        Assert.assertEquals( 5, targetRoot.getRoot().list().length );
        Assert.assertTrue( Files.exists( Paths.get( targetRoot.getRoot().getPath(), "src", "main", "resources", "site", "site.xml" ) ) );

        final Path buildGradlePath = Paths.get( targetRoot.getRoot().getPath(), "build.gradle" );
        final String buildGraphContent = new String( Files.readAllBytes( buildGradlePath ), StandardCharsets.UTF_8 );
        Assert.assertTrue( buildGraphContent.contains( "name = 'myapp'" ) );
        Assert.assertTrue( buildGraphContent.contains( "version = 'X.Y.Z'" ) );
    }
}
