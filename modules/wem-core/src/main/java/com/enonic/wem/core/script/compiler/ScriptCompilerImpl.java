package com.enonic.wem.core.script.compiler;

import javax.inject.Inject;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;

import com.enonic.wem.api.resource.Resource;

public final class ScriptCompilerImpl
    implements ScriptCompiler
{
    private final ScriptCache cache;

    @Inject
    public ScriptCompilerImpl( final ScriptCache cache )
    {
        this.cache = cache;
    }

    @Override
    public Script compile( final Context context, final Resource resource )
    {
        final String key = resource.getKey().toString() + "_" + resource.getTimestamp();
        final Script script = this.cache.get( key );
        if ( script != null )
        {
            return script;
        }

        final Script compiled = doCompile( context, resource );
        this.cache.put( key, compiled );
        return compiled;
    }

    private Script doCompile( final Context context, final Resource resource )
    {
        return context.compileString( resource.readAsString(), resource.getKey().toString(), 1, null );
    }
}
