package com.enonic.xp.vfs;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * http://docs.jboss.org/osgi/jboss-osgi-1.0.0/apidocs/org/jboss/osgi/vfs/VirtualFile.html
 */
@Ignore
public class PathBasedVirtualFileTest
    extends AbstractVirtualFileTest
{
    @Test
    public void testFolder()
    {
        final VirtualFile file = VirtualFiles.from( this.rootDir );

        assertTrue( file.exists() );
        assertFalse( file.isFile() );
        assertTrue( file.isFolder() );
        assertNull( file.getByteSource() );
        assertNull( file.getCharSource() );
        assertNotNull( file.getUrl() );
        assertEquals( "root", file.getName() );
        assertTrue( file.getPath().getPath().endsWith( "/root" ) );
        assertTrue( file.getUrl().toString().endsWith( "/root/" ) );
    }

    @Test
    public void testResolve()
    {
        final VirtualFile file1 = VirtualFiles.from( this.rootDir ).resolve( VirtualFilePaths.from( "dir1", "/" ) );
        assertTrue( file1.exists() );
        assertFalse( file1.isFile() );
        assertTrue( file1.isFolder() );

        final VirtualFile file2 = file1.resolve( VirtualFilePaths.from( "file1.txt", "/" ) );
        assertTrue( file2.exists() );
        assertTrue( file2.isFile() );
        assertFalse( file2.isFolder() );

        final VirtualFile file3 = VirtualFiles.from( this.rootDir ).resolve( VirtualFilePaths.from( "dir1/file1.txt", "/" ) );
        assertTrue( file3.exists() );
        assertTrue( file3.isFile() );
        assertFalse( file3.isFolder() );
    }

    @Test
    public void testFile()
        throws Exception
    {
        final VirtualFile file = VirtualFiles.from( this.rootDir ).resolve( VirtualFilePaths.from( "dir1/file1.txt", "/" ) );
        assertTrue( file.exists() );
        assertTrue( file.isFile() );
        assertFalse( file.isFolder() );
        assertNotNull( file.getChildren() );
        assertEquals( 0, file.getChildren().size() );
        assertEquals( "file1.txt", file.getName() );
        assertTrue( file.getPath().getPath().endsWith( "/file1.txt" ) );

        assertNotNull( file.getCharSource() );
        assertEquals( "contents of dir1/file1.txt", file.getCharSource().readFirstLine() );

        assertNotNull( file.getByteSource() );
        assertEquals( "contents of dir1/file1.txt", new String( file.getByteSource().read() ) );
    }

    @Test
    public void testGetChildren()
    {
        final VirtualFile file = VirtualFiles.from( this.rootDir ).resolve( VirtualFilePaths.from( "dir2", "/" ) );

        final List<VirtualFile> children = file.getChildren();
        assertNotNull( children );
        assertEquals( 3, children.size() );

        assertEquals( "dir3", children.get( 0 ).getName() );
        assertEquals( "file1.txt", children.get( 1 ).getName() );
        assertEquals( "file2.log", children.get( 2 ).getName() );
    }

    @Test
    public void testNotExists()
    {
        final VirtualFile file = VirtualFiles.from( this.rootDir ).resolve( VirtualFilePaths.from( "notFound", "/" ) );
        assertFalse( file.exists() );
        assertFalse( file.isFile() );
        assertFalse( file.isFolder() );
        assertNull( file.getByteSource() );
        assertNull( file.getCharSource() );
        assertNotNull( file.getUrl() );
        assertEquals( "notFound", file.getName() );
        assertTrue( file.getPath().getPath().endsWith( "/notFound" ) );
        assertTrue( file.getUrl().toString().endsWith( "/notFound" ) );
    }
}
