package com.enonic.xp.resource;

import org.junit.Test;

import static org.junit.Assert.*;

public class ResourceKeyResolverTest
{
    @Test
    public void resolveFromRoot()
    {
        final ResourceKeyResolver resolver = new ResourceKeyResolver( null );

        final ResourceKey key1 = resolver.resolve( ResourceKey.from( "myapp:/" ), "test.js" );
        assertEquals( "myapp:/test.js", key1.toString() );

        final ResourceKey key2 = resolver.resolve( ResourceKey.from( "myapp:/" ), "/some/folder/test.js" );
        assertEquals( "myapp:/some/folder/test.js", key2.toString() );

        final ResourceKey key3 = resolver.resolve( ResourceKey.from( "myapp:/some/folder" ), "./test.js" );
        assertEquals( "myapp:/some/test.js", key3.toString() );

        final ResourceKey key4 = resolver.resolve( ResourceKey.from( "myapp:/some/folder" ), "../test.js" );
        assertEquals( "myapp:/test.js", key4.toString() );
    }

    @Test
    public void resolveFromBase()
    {
        final ResourceKeyResolver resolver = new ResourceKeyResolver( "/site" );

        final ResourceKey key1 = resolver.resolve( ResourceKey.from( "myapp:/" ), "test.js" );
        assertEquals( "myapp:/site/test.js", key1.toString() );

        final ResourceKey key2 = resolver.resolve( ResourceKey.from( "myapp:/" ), "/some/folder/test.js" );
        assertEquals( "myapp:/site/some/folder/test.js", key2.toString() );

        final ResourceKey key3 = resolver.resolve( ResourceKey.from( "myapp:/site" ), "../test.js" );
        assertEquals( "myapp:/site/test.js", key3.toString() );

        final ResourceKey key4 = resolver.resolve( ResourceKey.from( "myapp:/site" ), "/some/folder/test.js" );
        assertEquals( "myapp:/site/some/folder/test.js", key4.toString() );

        final ResourceKey key5 = resolver.resolve( ResourceKey.from( "myapp:/some/folder" ), "./test.js" );
        assertEquals( "myapp:/site/some/test.js", key5.toString() );

        final ResourceKey key6 = resolver.resolve( ResourceKey.from( "myapp:/site/some/folder" ), "../test.js" );
        assertEquals( "myapp:/site/test.js", key6.toString() );
    }
}
