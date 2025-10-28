package com.enonic.xp.script.impl.function;

import org.junit.jupiter.api.Test;

import com.enonic.xp.resource.ResourceKey;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResourceResolverTest
    extends ResolverTestSupport
{
    @Test
    void resolve_absolute()
        throws Exception
    {
        touchFile( "dummy.txt" );

        final ResourceKey key1 = resolve( "/a/b/c.txt", "/dummy.txt" );
        assertEquals( "/dummy.txt", key1.getPath() );

        touchFile( "site/dummy.txt" );

        final ResourceKey key2 = resolve( "/a/b/c.txt", "/dummy.txt" );
        assertEquals( "/site/dummy.txt", key2.getPath() );
    }

    @Test
    void resolve_relative()
        throws Exception
    {
        touchFile( "dir1/dir2/dummy.txt" );
        touchFile( "dir1/dummy.txt" );

        final ResourceKey key1 = resolve( "/dir1/dir2/c.txt", "./dummy.txt" );
        assertEquals( "/dir1/dir2/dummy.txt", key1.getPath() );

        final ResourceKey key2 = resolve( "/dir1/dir2/c.txt", "../dummy.txt" );
        assertEquals( "/dir1/dummy.txt", key2.getPath() );

        final ResourceKey key3 = resolve( "/dir1/dir2/c.txt", "dummy.txt" );
        assertEquals( "/dir1/dir2/dummy.txt", key3.getPath() );
    }

    private ResourceKey resolve( final String base, final String path )
    {
        return resolver( base ).resolve( path );
    }

    private ResourceResolver resolver( final String base )
    {
        return new ResourceResolver( this.resourceService, ResourceKey.from( "foo:" + base ) );
    }
}
