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

        final ScriptExportsCache scriptExportsCache = new ScriptExportsCache( RunMode.PROD, resourceLookup, expiredCallback );
        final Object getOrCompute1 = scriptExportsCache.getOrCompute( resourceKey, requireFunction );
        final Object getOrCompute2 = scriptExportsCache.getOrCompute( resourceKey, requireFunction );

        assertAll( () -> assertSame( value, getOrCompute1 ), () -> assertSame( value, getOrCompute2 ) );

        verify( resourceLookup, times( 1 ) ).apply( resourceKey );
        verifyNoMoreInteractions( resourceLookup );

        verify( requireFunction, times( 1 ) ).apply( resource );
        verifyNoMoreInteractions( requireFunction );
    }

    @Test
    void expireCacheIfNeeded_has_no_effect_in_prod()
        throws Exception
    {
        final ResourceKey resourceKey = ResourceKey.from( ApplicationKey.from( "some.app" ), "main.js" );
        final Resource resource = mock( Resource.class );
        when( resource.getTimestamp() ).thenReturn( 1L, 2L );

        final Object value = new Object();
        when( requireFunction.apply( resource ) ).thenReturn( value );

        when( resourceLookup.apply( resourceKey ) ).thenReturn( resource );

        final ScriptExportsCache scriptExportsCache = new ScriptExportsCache( RunMode.PROD, resourceLookup, expiredCallback );
        final Object getOrCompute1 = scriptExportsCache.getOrCompute( resourceKey, requireFunction );
        scriptExportsCache.expireCacheIfNeeded();
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

        final ScriptExportsCache scriptExportsCache = new ScriptExportsCache( RunMode.DEV, resourceLookup, expiredCallback );
        final Object getOrCompute1 = scriptExportsCache.getOrCompute( resourceKey, requireFunction );
        scriptExportsCache.expireCacheIfNeeded();
        final Object getOrCompute2 = scriptExportsCache.getOrCompute( resourceKey, requireFunction );

        assertAll( () -> assertSame( value, getOrCompute1 ), () -> assertSame( value, getOrCompute2 ) );

        verify( resourceLookup, times( 2 ) ).apply( resourceKey );
        verifyNoMoreInteractions( resourceLookup );

        verify( requireFunction, times( 2 ) ).apply( resource );
        verifyNoMoreInteractions( requireFunction );
    }
}
