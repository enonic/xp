package com.enonic.xp.resource;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;

public class ResourceKeyTest
{
    @Test
    public void fromUri()
    {
        fromUri( "mymodule-1.0.0:", "mymodule-1.0.0:/", "mymodule-1.0.0", "/", "", null, true );
        fromUri( "mymodule-1.0.0:/", "mymodule-1.0.0:/", "mymodule-1.0.0", "/", "", null, true );
        fromUri( "mymodule-1.0.0:a/b.txt", "mymodule-1.0.0:/a/b.txt", "mymodule-1.0.0", "/a/b.txt", "b.txt", "txt", false );
        fromUri( "mymodule-1.0.0:/a/b.txt", "mymodule-1.0.0:/a/b.txt", "mymodule-1.0.0", "/a/b.txt", "b.txt", "txt", false );
        fromUri( "mymodule-1.0.0://a//b.txt", "mymodule-1.0.0:/a/b.txt", "mymodule-1.0.0", "/a/b.txt", "b.txt", "txt", false );
        fromUri( "mymodule-1.0.0://a/..", "mymodule-1.0.0:/", "mymodule-1.0.0", "/", "", null, true );
        fromUri( "mymodule-1.0.0://a/./b/..", "mymodule-1.0.0:/a", "mymodule-1.0.0", "/a", "a", null, false );
    }

    private void fromUri( final String input, final String uri, final String module, final String path, final String name, final String ext,
                          final boolean root )
    {
        final ResourceKey key = ResourceKey.from( input );

        Assert.assertNotNull( key );
        Assert.assertEquals( uri, key.toString() );
        Assert.assertEquals( uri, key.getUri() );
        Assert.assertEquals( path, key.getPath() );
        Assert.assertEquals( ext, key.getExtension() );
        Assert.assertEquals( module, key.getApplicationKey().toString() );
        Assert.assertEquals( root, key.isRoot() );
        Assert.assertEquals( name, key.getName() );
    }

    @Test
    public void fromModuleAndPath()
    {
        fromModuleAndPath( "", "mymodule-1.0.0:/", "/", null, true );
        fromModuleAndPath( "/", "mymodule-1.0.0:/", "/", null, true );
        fromModuleAndPath( "a/b.txt", "mymodule-1.0.0:/a/b.txt", "/a/b.txt", "txt", false );
        fromModuleAndPath( "/a/b.txt", "mymodule-1.0.0:/a/b.txt", "/a/b.txt", "txt", false );
        fromModuleAndPath( "//a//b.txt", "mymodule-1.0.0:/a/b.txt", "/a/b.txt", "txt", false );
        fromModuleAndPath( "//a/..", "mymodule-1.0.0:/", "/", null, true );
        fromModuleAndPath( "//a/./b/..", "mymodule-1.0.0:/a", "/a", null, false );
    }

    private void fromModuleAndPath( final String input, final String uri, final String path, final String ext, final boolean root )
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "mymodule-1.0.0" );
        final ResourceKey key = ResourceKey.from( applicationKey, input );

        Assert.assertNotNull( key );
        Assert.assertEquals( uri, key.toString() );
        Assert.assertEquals( uri, key.getUri() );
        Assert.assertEquals( path, key.getPath() );
        Assert.assertEquals( ext, key.getExtension() );
        Assert.assertEquals( applicationKey.toString(), key.getApplicationKey().toString() );
        Assert.assertEquals( root, key.isRoot() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidUri()
    {
        ResourceKey.from( "test" );
    }

    @Test
    public void testEquals()
    {
        testEquals( "mymodule-1.0.0:/", "mymodule-1.0.0:/", true );
        testEquals( "mymodule-1.0.0:", "mymodule-1.0.0:/", true );
        testEquals( "mymodule-1.0.0:/a/b", "mymodule-1.0.0:/a/b", true );
        testEquals( "mymodule-1.0.0:/a", "mymodule-1.0.0:/a/b", false );
        testEquals( "mymodule-1.0.0:/a/b", "mymodule-1.1.0:/a/b", false );
    }

    private void testEquals( final String key1, final String key2, final boolean flag )
    {
        final boolean result = ResourceKey.from( key1 ).equals( ResourceKey.from( key2 ) );
        Assert.assertEquals( flag, result );
    }

    @Test
    public void testResolve()
    {
        testResolve( "mymodule-1.0.0:/", "", "mymodule-1.0.0:/" );
        testResolve( "mymodule-1.0.0:/", ".", "mymodule-1.0.0:/" );
        testResolve( "mymodule-1.0.0:/", "/", "mymodule-1.0.0:/" );
        testResolve( "mymodule-1.0.0:/a/b", "../c", "mymodule-1.0.0:/a/c" );
        testResolve( "mymodule-1.0.0:/a", "b/c", "mymodule-1.0.0:/a/b/c" );
    }

    private void testResolve( final String uri, final String path, final String resolved )
    {
        final ResourceKey key1 = ResourceKey.from( uri );
        final ResourceKey key2 = key1.resolve( path );

        Assert.assertNotNull( key2 );
        Assert.assertEquals( resolved, key2.toString() );
    }

    @Test
    public void testHashCode()
    {
        final ResourceKey key1 = ResourceKey.from( "mymodule-1.0.0:/a/b" );
        final ResourceKey key2 = ResourceKey.from( "mymodule-1.0.0:/a/b" );
        final ResourceKey key3 = ResourceKey.from( "mymodule-1.0.0:/a" );

        Assert.assertEquals( key1.hashCode(), key2.hashCode() );
        Assert.assertNotEquals( key1.hashCode(), key3.hashCode() );
    }
}
