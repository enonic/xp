package com.enonic.xp.script.impl.executor;

import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.server.RunMode;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScriptExportsCacheTest
{
    @Mock
    Function<ResourceKey, Resource> resourceLookup;

    @Mock
    Runnable expiredCallback;

    @Mock
    Function<Resource, Object> requireFunction;

    @Test
    void getOrCompute_calls_require_only_once()
        throws Exception
    {
        final ResourceKey resourceKey = ResourceKey.from( ApplicationKey.from( "some.app" ), "main.js" );
        final Resource resource = mock( Resource.class );

        final Object value = new Object();
        when( requireFunction.apply( resource ) ).thenReturn( value );

        when( resourceLookup.apply( resourceKey ) ).thenReturn( resource );

        final ScriptExportsCache scriptExportsCache = new ScriptExportsCache( resourceLookup, expiredCallback );
        final Object getOrCompute1 = scriptExportsCache.getOrCompute( resourceKey, requireFunction );
        final Object getOrCompute2 = scriptExportsCache.getOrCompute( resourceKey, requireFunction );

        assertAll( () -> assertSame( value, getOrCompute1 ), () -> assertSame( value, getOrCompute2 ) );

        verify( resourceLookup, times( 1 ) ).apply( resourceKey );
        verifyNoMoreInteractions( resourceLookup );

        verify( requireFunction, times( 1 ) ).apply( resource );
        verifyNoMoreInteractions( requireFunction );
    }

    @Test
    void expireCacheIfNeeded()
        throws Exception
    {
        final ResourceKey resourceKey = ResourceKey.from( ApplicationKey.from( "some.app" ), "main.js" );
        final Resource resource = mock( Resource.class );
        when( resource.getTimestamp() ).thenReturn( 1L, 2L );

        final Object value = new Object();
        when( requireFunction.apply( resource ) ).thenReturn( value );

        when( resourceLookup.apply( resourceKey ) ).thenReturn( resource );

        final ScriptExportsCache scriptExportsCache = new ScriptExportsCache( resourceLookup, expiredCallback );
        final Object getOrCompute1 = scriptExportsCache.getOrCompute( resourceKey, requireFunction );
        scriptExportsCache.expireCacheIfNeeded();
        final Object getOrCompute2 = scriptExportsCache.getOrCompute( resourceKey, requireFunction );

        assertAll( () -> assertSame( value, getOrCompute1 ), () -> assertSame( value, getOrCompute2 ) );

        verify( resourceLookup, times( 2 ) ).apply( resourceKey );
        verifyNoMoreInteractions( resourceLookup );

        verify( requireFunction, times( 2 ) ).apply( resource );
        verifyNoMoreInteractions( requireFunction );
    }

    @Test
    void expireCacheIfNeeded_one_expired_clears_all()
        throws Exception
    {
        final ResourceKey resourceKey = ResourceKey.from( ApplicationKey.from( "some.app" ), "some0.js" );
        final Resource resource = mock( Resource.class );
        when( resource.getTimestamp() ).thenReturn( 1L );
        when( resourceLookup.apply( resourceKey ) ).thenReturn( resource );

        final ResourceKey resourceKeyExtra = ResourceKey.from( ApplicationKey.from( "some.app" ), "some1.js" );
        final Resource resourceExtra = mock( Resource.class );
        when( resourceExtra.getTimestamp() ).thenReturn( 2L, 3L );
        when( resourceLookup.apply( resourceKeyExtra ) ).thenReturn( resourceExtra );

        final ScriptExportsCache scriptExportsCache = new ScriptExportsCache( resourceLookup, expiredCallback );
        scriptExportsCache.getOrCompute( resourceKey, requireFunction );
        scriptExportsCache.getOrCompute( resourceKeyExtra, requireFunction );
        scriptExportsCache.expireCacheIfNeeded();
        scriptExportsCache.getOrCompute( resourceKey, requireFunction );

        verify( resourceExtra, times( 2 ) ).getTimestamp();
        verify( resourceLookup, times( 2 ) ).apply( resourceKey );
        verify( requireFunction, times( 2 ) ).apply( resource );
    }
}
