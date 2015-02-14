package com.enonic.xp.portal.impl.controller;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.enonic.xp.core.resource.Resource;
import com.enonic.xp.core.resource.ResourceKey;
import com.enonic.xp.portal.postprocess.PostProcessor;
import com.enonic.xp.portal.script.ScriptExports;
import com.enonic.xp.portal.script.ScriptService;

@Component
public final class ControllerScriptFactoryImpl
    implements ControllerScriptFactory
{
    private final Cache<String, ControllerScriptImpl> cache;

    private ScriptService scriptService;

    private PostProcessor postProcessor;

    // TODO: Make caching better. Invaliate on module change.
    public ControllerScriptFactoryImpl()
    {
        this.cache = CacheBuilder.newBuilder().maximumSize( 100 ).build();
    }

    @Override
    public ControllerScript newController( final ResourceKey scriptDir )
    {
        final ResourceKey script = scriptDir.resolve( "controller.js" );
        return getOrCreate( script );
    }

    private ControllerScriptImpl getOrCreate( final ResourceKey script )
    {
        final String cacheKey = composeCacheKey( script );
        final ControllerScriptImpl controller = this.cache.getIfPresent( cacheKey );
        if ( controller != null )
        {
            return controller;
        }

        final ControllerScriptImpl created = createControllerScript( script );
        this.cache.put( cacheKey, created );
        return created;
    }

    private ControllerScriptImpl createControllerScript( final ResourceKey script )
    {
        final ScriptExports exports = this.scriptService.execute( script );
        return new ControllerScriptImpl( exports, this.postProcessor );
    }

    private String composeCacheKey( final ResourceKey script )
    {
        return script.toString() + "_" + Resource.from( script ).getTimestamp();
    }

    @Reference
    public void setScriptService( final ScriptService scriptService )
    {
        this.scriptService = scriptService;
    }

    @Reference
    public void setPostProcessor( final PostProcessor postProcessor )
    {
        this.postProcessor = postProcessor;
    }
}
