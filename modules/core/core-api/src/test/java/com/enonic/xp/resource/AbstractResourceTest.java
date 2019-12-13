package com.enonic.xp.resource;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;

import com.google.common.io.ByteSource;

public class AbstractResourceTest
{
    public Path temporaryFolder;

    protected File applicationsDir;

    private void writeFile( final File dir, final String path, final String value )
        throws Exception
    {
        final File file = new File( dir, path );
        file.getParentFile().mkdirs();
        try (final FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            ByteSource.wrap( value.getBytes( StandardCharsets.UTF_8 ) ).copyTo( fileOutputStream );
        }
    }

    @BeforeEach
    public void setup()
        throws Exception
    {
        //TODO @TempDir JUnit5 suits better, but tests fail due to https://bugs.openjdk.java.net/browse/JDK-6956385
        temporaryFolder = Files.createTempDirectory("abstractResourceTest");

        applicationsDir = Files.createDirectory(this.temporaryFolder.resolve( "applications" ) ).toFile();

        writeFile( applicationsDir, "myapplication/a/b.txt", "a/b.txt" );
    }
}
