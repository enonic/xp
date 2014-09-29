package com.enonic.wem.portal.internal.controller;

import javax.inject.Inject;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.portal.internal.postprocess.PostProcessor;
import com.enonic.wem.script.ScriptExports;
import com.enonic.wem.script.ScriptService;

public final class JsControllerFactoryImpl
    implements JsControllerFactory
{
    private final Cache<String, JsControllerImpl> cache;

    private ScriptService scriptService;

    private PostProcessor postProcessor;

    // TODO: Make caching better. Invaliate on module change.
    public JsControllerFactoryImpl()
    {
        this.cache = CacheBuilder.newBuilder().maximumSize( 100 ).build();
    }

    @Override
    public JsController newController( final ResourceKey scriptDir )
    {
        final ResourceKey script = scriptDir.resolve( "controller.js" );
        return getOrCreate( script );
    }

    private JsControllerImpl getOrCreate( final ResourceKey script )
    {
        final String cacheKey = composeCacheKey( script );
        final JsControllerImpl controller = this.cache.getIfPresent( cacheKey );
        if ( controller != null )
        {
            return controller;
        }

        final JsControllerImpl created = createController( script );
        this.cache.put( cacheKey, created );
        return created;
    }

    private JsControllerImpl createController( final ResourceKey script )
    {
        final ScriptExports exports = this.scriptService.execute( script );
        return new JsControllerImpl( exports, this.postProcessor );
    }

    private String composeCacheKey( final ResourceKey script )
    {
        return script.toString() + "_" + Resource.from( script ).getTimestamp();
    }

    @Inject
    public void setScriptService( final ScriptService scriptService )
    {
        this.scriptService = scriptService;
    }

    @Inject
    public void setPostProcessor( final PostProcessor postProcessor )
    {
        this.postProcessor = postProcessor;
    }
}
