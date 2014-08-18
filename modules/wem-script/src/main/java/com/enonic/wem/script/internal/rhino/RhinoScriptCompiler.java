package com.enonic.wem.script.internal.rhino;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;

final class RhinoScriptCompiler
{
    private final Cache<String, Script> cache;

    public RhinoScriptCompiler()
    {
        this.cache = CacheBuilder.newBuilder().maximumSize( 1000 ).build();
    }

    public Script compile( final Context context, final ResourceKey source )
    {
        final Resource resource = Resource.from( source );
        final String key = source.toString() + "_" + resource.getTimestamp();
        final Script script = this.cache.getIfPresent( key );
        if ( script != null )
        {
            return script;
        }

        final Script compiled = doCompile( context, resource );
        this.cache.put( key, compiled );
        return compiled;
    }

    private Script doCompile( final Context context, final Resource source )
    {
        return context.compileString( source.readString(), source.getKey().toString(), 1, null );
    }
}
