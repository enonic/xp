package com.enonic.wem.core.module.source;

import java.net.URL;

import org.junit.Test;

import com.google.common.base.Charsets;

import com.enonic.wem.api.module.ModuleResourceKey;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

public class ModuleSourceImplTest
{
    @Test
    public void testExists()
        throws Exception
    {
        final URL url = getClass().getResource( "testSource.txt" );
        assertNotNull( url );

        final ModuleResourceKey key = ModuleResourceKey.from( "mymodule-1.0.0:js/my.js" );
        final ModuleSourceImpl source = new ModuleSourceImpl( key, url );

        assertEquals( key.toString(), source.getUri() );
        assertEquals( key, source.getKey() );
        assertEquals( key.toString(), source.toString() );
        assertEquals( url, source.getResolvedUrl() );
        assertTrue( source.exists() );

        assertEquals( "Test file.", source.getBytes().asCharSource( Charsets.UTF_8 ).read() );
        assertEquals( url.openConnection().getLastModified(), source.getTimestamp() );
    }

    @Test(expected = SourceNotFoundException.class)
    public void testNotExists()
    {
        final ModuleResourceKey key = ModuleResourceKey.from( "mymodule-1.0.0:js/my.js" );
        final ModuleSourceImpl source = new ModuleSourceImpl( key, null );

        assertEquals( key.toString(), source.getUri() );
        assertEquals( key, source.getKey() );
        assertEquals( key.toString(), source.toString() );
        assertNull( source.getResolvedUrl() );
        assertEquals( 0, source.getTimestamp() );
        assertFalse( source.exists() );

        source.getBytes();
    }
}
