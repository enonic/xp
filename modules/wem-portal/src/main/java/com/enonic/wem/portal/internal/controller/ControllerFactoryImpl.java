package com.enonic.wem.portal.internal.controller;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.portal.internal.postprocess.PostProcessor;
import com.enonic.wem.script.ScriptExports;
import com.enonic.wem.script.ScriptService;

public final class ControllerFactoryImpl
    implements ControllerFactory
{
    private final Cache<String, ControllerImpl> cache;

    private ScriptService scriptService;

    private PostProcessor postProcessor;

    // TODO: Make caching better. Invaliate on module change.
    public ControllerFactoryImpl()
    {
        this.cache = CacheBuilder.newBuilder().maximumSize( 100 ).build();
    }

    @Override
    public Controller newController( final ResourceKey scriptDir )
    {
        final ResourceKey script = scriptDir.resolve( "controller.js" );
        return getOrCreate( script );
    }

    private ControllerImpl getOrCreate( final ResourceKey script )
    {
        final String cacheKey = composeCacheKey( script );
        final ControllerImpl controller = this.cache.getIfPresent( cacheKey );
        if ( controller != null )
        {
            return controller;
        }

        final ControllerImpl created = createController( script );
        this.cache.put( cacheKey, created );
        return created;
    }

    private ControllerImpl createController( final ResourceKey script )
    {
        final ScriptExports exports = this.scriptService.execute( script );
        return new ControllerImpl( exports, this.postProcessor );
    }

    private String composeCacheKey( final ResourceKey script )
    {
        return script.toString() + "_" + Resource.from( script ).getTimestamp();
    }

    public void setScriptService( final ScriptService scriptService )
    {
        this.scriptService = scriptService;
    }

    public void setPostProcessor( final PostProcessor postProcessor )
    {
        this.postProcessor = postProcessor;
    }
}
