package com.enonic.xp.resource;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;

public class ResourceKeyTest
{
    @Test
    public void fromUri()
    {
        fromUri( "myapplication-1.0.0:", "myapplication-1.0.0:/", "myapplication-1.0.0", "/", "", null, true );
        fromUri( "myapplication-1.0.0:/", "myapplication-1.0.0:/", "myapplication-1.0.0", "/", "", null, true );
        fromUri( "myapplication-1.0.0:a/b.txt", "myapplication-1.0.0:/a/b.txt", "myapplication-1.0.0", "/a/b.txt", "b.txt", "txt", false );
        fromUri( "myapplication-1.0.0:/a/b.txt", "myapplication-1.0.0:/a/b.txt", "myapplication-1.0.0", "/a/b.txt", "b.txt", "txt", false );
        fromUri( "myapplication-1.0.0://a//b.txt", "myapplication-1.0.0:/a/b.txt", "myapplication-1.0.0", "/a/b.txt", "b.txt", "txt",
                 false );
        fromUri( "myapplication-1.0.0://a/..", "myapplication-1.0.0:/", "myapplication-1.0.0", "/", "", null, true );
        fromUri( "myapplication-1.0.0://a/./b/..", "myapplication-1.0.0:/a", "myapplication-1.0.0", "/a", "a", null, false );
    }

    private void fromUri( final String input, final String uri, final String application, final String path, final String name,
                          final String ext,
                          final boolean root )
    {
        final ResourceKey key = ResourceKey.from( input );

        assertNotNull( key );
        assertEquals( uri, key.toString() );
        assertEquals( uri, key.getUri() );
        assertEquals( path, key.getPath() );
        assertEquals( ext, key.getExtension() );
        assertEquals( application, key.getApplicationKey().toString() );
        assertEquals( root, key.isRoot() );
        assertEquals( name, key.getName() );
    }

    @Test
    public void fromApplicationAndPath()
    {
        fromApplicationAndPath( "", "myapplication-1.0.0:/", "/", null, true );
        fromApplicationAndPath( "/", "myapplication-1.0.0:/", "/", null, true );
        fromApplicationAndPath( "a/b.txt", "myapplication-1.0.0:/a/b.txt", "/a/b.txt", "txt", false );
        fromApplicationAndPath( "/a/b.txt", "myapplication-1.0.0:/a/b.txt", "/a/b.txt", "txt", false );
        fromApplicationAndPath( "//a//b.txt", "myapplication-1.0.0:/a/b.txt", "/a/b.txt", "txt", false );
        fromApplicationAndPath( "//a/..", "myapplication-1.0.0:/", "/", null, true );
        fromApplicationAndPath( "//a/./b/..", "myapplication-1.0.0:/a", "/a", null, false );
    }

    private void fromApplicationAndPath( final String input, final String uri, final String path, final String ext, final boolean root )
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapplication-1.0.0" );
        final ResourceKey key = ResourceKey.from( applicationKey, input );

        assertNotNull( key );
        assertEquals( uri, key.toString() );
        assertEquals( uri, key.getUri() );
        assertEquals( path, key.getPath() );
        assertEquals( ext, key.getExtension() );
        assertEquals( applicationKey.toString(), key.getApplicationKey().toString() );
        assertEquals( root, key.isRoot() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidUri()
    {
        ResourceKey.from( "test" );
    }

    @Test
    public void testEquals()
    {
        testEquals( "myapplication-1.0.0:/", "myapplication-1.0.0:/", true );
        testEquals( "myapplication-1.0.0:", "myapplication-1.0.0:/", true );
        testEquals( "myapplication-1.0.0:/a/b", "myapplication-1.0.0:/a/b", true );
        testEquals( "myapplication-1.0.0:/a", "myapplication-1.0.0:/a/b", false );
        testEquals( "myapplication-1.0.0:/a/b", "myapplication-1.1.0:/a/b", false );
    }

    private void testEquals( final String key1, final String key2, final boolean flag )
    {
        final boolean result = ResourceKey.from( key1 ).equals( ResourceKey.from( key2 ) );
        assertEquals( flag, result );
    }

    @Test
    public void testResolve()
    {
        testResolve( "myapplication-1.0.0:/", "", "myapplication-1.0.0:/" );
        testResolve( "myapplication-1.0.0:/", ".", "myapplication-1.0.0:/" );
        testResolve( "myapplication-1.0.0:/", "/", "myapplication-1.0.0:/" );
        testResolve( "myapplication-1.0.0:/a/b", "../c", "myapplication-1.0.0:/a/c" );
        testResolve( "myapplication-1.0.0:/a", "b/c", "myapplication-1.0.0:/a/b/c" );
    }

    private void testResolve( final String uri, final String path, final String resolved )
    {
        final ResourceKey key1 = ResourceKey.from( uri );
        final ResourceKey key2 = key1.resolve( path );

        assertNotNull( key2 );
        assertEquals( resolved, key2.toString() );
    }

    @Test
    public void testHashCode()
    {
        final ResourceKey key1 = ResourceKey.from( "myapplication-1.0.0:/a/b" );
        final ResourceKey key2 = ResourceKey.from( "myapplication-1.0.0:/a/b" );
        final ResourceKey key3 = ResourceKey.from( "myapplication-1.0.0:/a" );

        assertEquals( key1.hashCode(), key2.hashCode() );
        Assert.assertNotEquals( key1.hashCode(), key3.hashCode() );
    }
}
