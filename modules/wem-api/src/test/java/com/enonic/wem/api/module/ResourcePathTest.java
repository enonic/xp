package com.enonic.wem.api.module;

import java.util.Iterator;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

public class ResourcePathTest
{

    @Test
    public void testElements()
        throws Exception
    {
        assertEquals( 0, ResourcePath.from( "/" ).getElementCount() );
        assertEquals( 1, ResourcePath.from( "oneelement" ).getElementCount() );
        assertEquals( 2, ResourcePath.from( "two/elements" ).getElementCount() );
        assertEquals( 3, ResourcePath.from( "more/than/two" ).getElementCount() );

        assertEquals( 1, ResourcePath.from( "/oneelement" ).getElementCount() );
        assertEquals( 2, ResourcePath.from( "/two/elements" ).getElementCount() );
        assertEquals( 3, ResourcePath.from( "/more/than/two" ).getElementCount() );

        assertEquals( "one", ResourcePath.from( "one/two/three" ).getElement( 0 ) );
        assertEquals( "two", ResourcePath.from( "one/two/three" ).getElement( 1 ) );
        assertEquals( "three", ResourcePath.from( "one/two/three" ).getElement( 2 ) );
    }

    @Test
    public void testName()
        throws Exception
    {
        assertEquals( "oneelement", ResourcePath.from( "oneelement" ).getName() );
        assertEquals( "elements", ResourcePath.from( "two/elements" ).getName() );
        assertEquals( "two", ResourcePath.from( "more/than/two" ).getName() );
        assertEquals( "name", ResourcePath.from( "/absolute/path/name" ).getName() );
    }

    @Test
    public void testAbsolutePath()
        throws Exception
    {
        final ResourcePath absPath = ResourcePath.from( "/some/path/here" );
        final ResourcePath relPath = ResourcePath.from( "some/path/here" );

        assertTrue( absPath.isAbsolute() );
        assertFalse( absPath.isRelative() );
        assertTrue( relPath.isRelative() );
        assertFalse( relPath.isAbsolute() );

        assertTrue( absPath.toAbsolutePath().isAbsolute() );
        assertTrue( relPath.toAbsolutePath().isAbsolute() );

        assertTrue( absPath.toRelativePath().isRelative() );
        assertTrue( relPath.toRelativePath().isRelative() );
    }

    @Test
    public void testRoot()
        throws Exception
    {
        assertTrue( ResourcePath.root().isRoot() );
        assertTrue( ResourcePath.from( "/" ).isRoot() );
        assertFalse( ResourcePath.from( "path" ).isRoot() );
        assertFalse( ResourcePath.from( "/path" ).isRoot() );
    }

    @Test
    public void testStartsWith()
        throws Exception
    {
        final ResourcePath path1 = ResourcePath.from( "/some/path/here" );
        final ResourcePath basePath = ResourcePath.from( "/some/path" );
        final ResourcePath basePath2 = ResourcePath.from( "/some/other" );

        assertTrue( path1.startsWith( basePath ) );
        assertTrue( path1.startsWith( "/some/path" ) );
        assertTrue( path1.startsWith( path1 ) );
        assertTrue( path1.startsWith( ResourcePath.root() ) );
        assertFalse( path1.startsWith( basePath2 ) );
    }

    @Test
    public void testEndsWith()
        throws Exception
    {
        final ResourcePath path = ResourcePath.from( "/some/path/here" );
        final ResourcePath endPath1 = ResourcePath.from( "here" );
        final ResourcePath endPath2 = ResourcePath.from( "path/here" );
        final ResourcePath endPath3 = ResourcePath.from( "some/path/here" );
        final ResourcePath otherPath = ResourcePath.from( "/some/path" );
        final ResourcePath otherPath2 = ResourcePath.from( "path" );

        assertTrue( path.endsWith( endPath1 ) );
        assertTrue( path.endsWith( "here" ) );
        assertTrue( path.endsWith( endPath2 ) );
        assertTrue( path.endsWith( endPath3 ) );
        assertFalse( path.endsWith( otherPath ) );
        assertFalse( path.endsWith( otherPath2 ) );
    }

    @Test
    public void testResolve()
        throws Exception
    {
        final ResourcePath basePath = ResourcePath.from( "/base/path" );
        final ResourcePath relPath = ResourcePath.from( "some/files" );

        assertEquals( "/base/path/some/files", basePath.resolve( relPath ).toString() );
        assertEquals( "/base/path/file", basePath.resolve( "file" ).toString() );
    }

    @Test
    public void testEquals()
        throws Exception
    {
        assertTrue( ResourcePath.root().equals( ResourcePath.from( "/" ) ) );
        assertTrue( ResourcePath.from( "oneelement" ).equals( ResourcePath.from( "oneelement" ) ) );
        assertTrue( ResourcePath.from( "two/elements" ).equals( ResourcePath.from( "two/elements" ) ) );
        assertTrue( ResourcePath.from( "more/than/two" ).equals( ResourcePath.from( "more/than/two" ) ) );
        final ResourcePath path = ResourcePath.from( "/my/path" );
        assertTrue( path.equals( path ) );

        assertTrue( ResourcePath.from( "/oneelement" ).equals( ResourcePath.from( "/oneelement" ) ) );
        assertTrue( ResourcePath.from( "/two/elements" ).equals( ResourcePath.from( "/two/elements" ) ) );
        assertTrue( ResourcePath.from( "/more/than/two" ).equals( ResourcePath.from( "/more/than/two" ) ) );

        assertFalse( ResourcePath.from( "oneelement" ).equals( ResourcePath.from( "/oneelement" ) ) );
        assertFalse( ResourcePath.from( "two/elements" ).equals( ResourcePath.from( "/two/elements" ) ) );

        assertEquals( ResourcePath.from( "more/than/two" ).toString(), ResourcePath.from( "more/than/two" ).toString() );
        assertFalse( ResourcePath.from( "more/than/two" ).toString().equals( ResourcePath.from( "/more/than/two" ).toString() ) );
    }

    @Test
    public void testHashCode()
        throws Exception
    {
        assertTrue( ResourcePath.root().hashCode() == ResourcePath.from( "/" ).hashCode() );
        assertTrue( ResourcePath.from( "oneelement" ).hashCode() == ResourcePath.from( "oneelement" ).hashCode() );
        assertTrue( ResourcePath.from( "two/elements" ).hashCode() == ResourcePath.from( "two/elements" ).hashCode() );
        assertTrue( ResourcePath.from( "more/than/two" ).hashCode() == ResourcePath.from( "more/than/two" ).hashCode() );

        assertTrue( ResourcePath.from( "/oneelement" ).hashCode() == ResourcePath.from( "/oneelement" ).hashCode() );
        assertTrue( ResourcePath.from( "/two/elements" ).hashCode() == ResourcePath.from( "/two/elements" ).hashCode() );
        assertTrue( ResourcePath.from( "/more/than/two" ).hashCode() == ResourcePath.from( "/more/than/two" ).hashCode() );

        assertFalse( ResourcePath.from( "oneelement" ).hashCode() == ResourcePath.from( "/oneelement" ).hashCode() );
        assertFalse( ResourcePath.from( "two/elements" ).hashCode() == ResourcePath.from( "/two/elements" ).hashCode() );
    }

    @Test
    public void testIterator()
        throws Exception
    {
        final ResourcePath path1 = ResourcePath.from( "/base/path/file" );
        final Iterator<String> iterator1 = path1.iterator();
        assertTrue( iterator1.hasNext() );
        assertEquals( "base", iterator1.next() );
        assertTrue( iterator1.hasNext() );
        assertEquals( "path", iterator1.next() );
        assertTrue( iterator1.hasNext() );
        assertEquals( "file", iterator1.next() );
        assertFalse( iterator1.hasNext() );

        final ResourcePath path2 = ResourcePath.from( "/" );
        final Iterator<String> iterator2 = path2.iterator();
        assertFalse( iterator2.hasNext() );
    }

    @Test
    public void testParent()
        throws Exception
    {
        final ResourcePath path = ResourcePath.from( "/base/path/file" );
        final ResourcePath parent = path.parent();
        final ResourcePath grandParent = parent.parent();
        assertEquals( "/base/path", parent.toString() );
        assertEquals( "/base", grandParent.toString() );
        assertEquals( "/", grandParent.parent().toString() );
        assertNull( grandParent.parent().parent() );

        assertEquals( path, path.parent().resolve( "file" ) );
        assertEquals( path, path.parent().parent().resolve( "path" ).resolve( "file" ) );
    }
}
