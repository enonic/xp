package com.enonic.xp.resource;

import java.io.File;
import java.io.FileOutputStream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;

public class AbstractResourceTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    protected File applicationsDir;

    private void writeFile( final File dir, final String path, final String value )
        throws Exception
    {
        final File file = new File( dir, path );
        file.getParentFile().mkdirs();
        ByteSource.wrap( value.getBytes( Charsets.UTF_8 ) ).copyTo( new FileOutputStream( file ) );
    }

    @Before
    public void setup()
        throws Exception
    {
        applicationsDir = this.temporaryFolder.newFolder( "applications" );

        writeFile( applicationsDir, "myapplication/a/b.txt", "a/b.txt" );
    }
}
