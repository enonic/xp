package com.enonic.wem.api.vfs;

import java.net.URL;
import java.net.URLClassLoader;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

// https://github.com/kurtharriger/spring-osgi/blob/master/io/src/main/java/org/springframework/osgi/io/OsgiBundleResource.java
// https://github.com/kurtharriger/spring-osgi/blob/master/io/src/test/java/org/springframework/osgi/io/OsgiBundleResourceTest.java

public class ClassLoaderVirtualFileTest
    extends AbstractVirtualFileTest
{
    @Test
    public void testFolder()
        throws Exception
    {
        final URLClassLoader loader = new URLClassLoader( new URL[]{this.rootDir.toURI().toURL()}, null );
        final VirtualFile file = VirtualFiles.from( loader );

        assertTrue( file.exists() );
        assertFalse( file.isFile() );
        assertTrue( file.isFolder() );
        assertNull( file.getByteSource() );
        assertNull( file.getCharSource() );
        assertNotNull( file.getUrl() );
        assertEquals( "", file.getName() );
        assertEquals( "/", file.getPath().getPath() );
        assertTrue( file.getUrl().toString().endsWith( "/root/" ) );
    }

    @Test
    @Ignore
    public void testResolve()
        throws Exception
    {
        final URLClassLoader loader = new URLClassLoader( new URL[]{this.rootDir.toURI().toURL()}, null );

        final VirtualFile file1 = VirtualFiles.from( loader ).resolve( "dir1" );
        assertTrue( file1.exists() );
        assertFalse( file1.isFile() );
        assertTrue( file1.isFolder() );

        final VirtualFile file2 = file1.resolve( "file1.txt" );
        assertTrue( file2.exists() );
        assertTrue( file2.isFile() );
        assertFalse( file2.isFolder() );

        final VirtualFile file3 = VirtualFiles.from( loader ).resolve( "dir1/file1.txt" );
        assertTrue( file3.exists() );
        assertTrue( file3.isFile() );
        assertFalse( file3.isFolder() );
    }
}
