package com.enonic.xp.script.impl.function;

import org.junit.Test;

import com.enonic.xp.resource.ResourceKey;

import static org.junit.Assert.*;

public class RequireResolverTest
    extends ResolverTestSupport
{
    @Test
    public void resolve_js()
        throws Exception
    {
        touchFile( "/site/dummy.js" );

        final ResourceKey key1 = resolve( "/a/b/c.js", "/dummy.js" );
        assertEquals( "/site/dummy.js", key1.getPath() );

        touchFile( "dummy.js" );

        final ResourceKey key2 = resolve( "/a/b/c.js", "/dummy.js" );
        assertEquals( "/dummy.js", key2.getPath() );

        final ResourceKey key3 = resolve( "/c.js", "./dummy.js" );
        assertEquals( "/dummy.js", key3.getPath() );

        final ResourceKey key4 = resolve( "/dir1/c.js", "../dummy.js" );
        assertEquals( "/dummy.js", key4.getPath() );

        final ResourceKey key5 = resolve( "/dir1/dir2/c.txt", "dummy.txt" );
        assertEquals( "/dir1/dir2/dummy.txt", key5.getPath() );
    }

    @Test
    public void resolve_no_ext_js()
        throws Exception
    {
        touchFile( "/site/dummy/index.js" );

        final ResourceKey key1 = resolve( "/a/b/c.js", "/dummy" );
        assertEquals( "/site/dummy/index.js", key1.getPath() );

        touchFile( "dummy/index.js" );

        final ResourceKey key2 = resolve( "/a/b/c.js", "/dummy" );
        assertEquals( "/dummy/index.js", key2.getPath() );
    }

    @Test
    public void resolve_no_ext_json()
        throws Exception
    {
        touchFile( "/site/dummy/index.json" );

        final ResourceKey key1 = resolve( "/a/b/c.js", "/dummy" );
        assertEquals( "/site/dummy/index.json", key1.getPath() );

        touchFile( "dummy/index.json" );

        final ResourceKey key2 = resolve( "/a/b/c.js", "/dummy" );
        assertEquals( "/dummy/index.json", key2.getPath() );
    }

    @Test
    public void resolve_lib()
        throws Exception
    {
        touchFile( "/site/lib/dummy.js" );

        final ResourceKey key1 = resolve( "/a/b/c.js", "dummy" );
        assertEquals( "/site/lib/dummy.js", key1.getPath() );

        touchFile( "/lib/dummy.js" );

        final ResourceKey key2 = resolve( "/a/b/c.js", "dummy" );
        assertEquals( "/lib/dummy.js", key2.getPath() );
    }

    @Test
    public void findAllSearchPaths()
    {
        assertEquals( "[/a.js, /site/a.js]", RequireResolver.findAllSearchPaths( "/a.js" ).toString() );
        assertEquals( "[/a.js, /a/index.js, /a.json, /a/index.json, /site/a.js, /site/a/index.js, /site/a.json, /site/a/index.json]",
                      RequireResolver.findAllSearchPaths( "/a" ).toString() );
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
