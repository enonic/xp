package com.enonic.wem.core.script.cache;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.script.CompiledScript;

import com.google.common.base.Throwables;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public final class ScriptCacheImpl
    implements ScriptCache
{
    private final Cache<String, CompiledScript> cache;

    public ScriptCacheImpl()
    {
        this.cache = CacheBuilder.newBuilder().maximumSize( 1000 ).build();
    }

    @Override
    public CompiledScript get( String key, Callable<CompiledScript> loader )
    {
        try
        {
            return this.cache.get( key, loader );
        }
        catch ( final ExecutionException e )
        {
            final Throwable cause = e.getCause();
            if ( cause instanceof RuntimeException )
            {
                throw (RuntimeException) cause;
            }

            throw Throwables.propagate( e );
        }
    }
}
