package com.enonic.xp.script.impl.function;

import org.junit.jupiter.api.Test;

import com.enonic.xp.resource.ResourceKey;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequireResolverTest
    extends ResolverTestSupport
{
    @Test
    void resolve_js()
    {
        final ResourceKey key = resolve( "/a/b/c.js", "/dummy.js" );
        assertEquals( "/dummy.js", key.getPath() );

        final ResourceKey key2 = resolve( "/c.js", "./dummy.js" );
        assertEquals( "/dummy.js", key2.getPath() );

        final ResourceKey key3 = resolve( "/dir1/c.js", "../dummy.js" );
        assertEquals( "/dummy.js", key3.getPath() );

        final ResourceKey key4 = resolve( "/dir1/dir2/c.txt", "dummy.txt" );
        assertEquals( "/dir1/dir2/dummy.txt", key4.getPath() );
    }

    @Test
    void resolve_no_ext_js()
        throws Exception
    {
        touchFile( "dummy/index.js" );

        final ResourceKey key2 = resolve( "/a/b/c.js", "/dummy" );
        assertEquals( "/dummy/index.js", key2.getPath() );
    }

    @Test
    void resolve_no_ext_json()
        throws Exception
    {
        touchFile( "dummy/index.json" );

        final ResourceKey key2 = resolve( "/a/b/c.js", "/dummy" );
        assertEquals( "/dummy/index.json", key2.getPath() );
    }

    @Test
    void resolve_relative()
        throws Exception
    {
        touchFile( "a/b/dummy.js" );

        final ResourceKey key2 = resolve( "/a/b/c.js", "dummy" );
        assertEquals( "/a/b/dummy.js", key2.getPath() );
    }

    @Test
    void findAllSearchPaths()
    {
        assertEquals( "[/a.js]", RequireResolver.findSearchPaths( "/a.js" ).toString() );
        assertEquals( "[/a.js, /a/index.js, /a.json, /a/index.json]", RequireResolver.findSearchPaths( "/a" ).toString() );
    }

    private ResourceKey resolve( final String base, final String path )
    {
        return resolver( base ).resolve( path );
    }

    private RequireResolver resolver( final String base )
    {
        return new RequireResolver( this.resourceService, ResourceKey.from( "myapp:" + base ) );
    }
}
