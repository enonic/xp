package com.enonic.xp.resource;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;

public class AbstractResourceTest
{
    @TempDir
    public Path temporaryFolder;

    protected File applicationsDir;

    private void writeFile( final File dir, final String path, final String value )
        throws Exception
    {
        final File file = new File( dir, path );
        file.getParentFile().mkdirs();
        ByteSource.wrap( value.getBytes( Charsets.UTF_8 ) ).copyTo( new FileOutputStream( file ) );
    }

    @BeforeEach
    public void setup()
        throws Exception
    {
        applicationsDir = Files.createDirectory(this.temporaryFolder.resolve( "applications" ) ).toFile();

        writeFile( applicationsDir, "myapplication/a/b.txt", "a/b.txt" );
    }
}
