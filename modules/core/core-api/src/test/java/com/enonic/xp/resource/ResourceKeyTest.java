package com.enonic.xp.resource;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import com.enonic.xp.app.ApplicationKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResourceKeyTest
{
    @Test
    void fromUri()
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
                          final String ext, final boolean root )
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
    void fromApplicationAndPath()
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

    @Test
    void invalidUri()
    {
        assertThrows( IllegalArgumentException.class, () -> ResourceKey.from( "test" ) );
    }

    @Test
    void testResolve()
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
    void equalsContract()
    {
        EqualsVerifier.forClass( ResourceKey.class ).withNonnullFields( "applicationKey", "path" ).verify();
    }
}
