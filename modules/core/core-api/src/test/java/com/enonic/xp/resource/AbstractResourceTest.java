package com.enonic.xp.resource;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;

public class AbstractResourceTest
{
    public Path temporaryFolder;

    protected Path applicationsDir;

    private void writeFile( final Path dir, final String path, final String value )
        throws Exception
    {
        final Path file = dir.resolve( path );
        Files.createDirectories( file.getParent() );

        Files.writeString( file, value );
    }

    @BeforeEach
    public void setup()
        throws Exception
    {
        //TODO @TempDir JUnit5 suits better, but tests fail due to https://bugs.openjdk.java.net/browse/JDK-6956385
        temporaryFolder = Files.createTempDirectory("abstractResourceTest");

        applicationsDir = Files.createDirectory( this.temporaryFolder.resolve( "applications" ) );

        writeFile( applicationsDir, "myapplication/a/b.txt", "a/b.txt" );
    }
}
