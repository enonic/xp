package com.enonic.wem.portal.script.compiler;

import javax.inject.Inject;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Script;

import com.enonic.wem.portal.script.cache.ScriptCache;
import com.enonic.wem.portal.script.EvalScriptException;
import com.enonic.wem.portal.script.loader.ScriptSource;

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
    public Script compile( final Context context, final ScriptSource source )
    {
        final Script script = this.cache.get( source.getCacheKey() );
        if ( script != null )
        {
            return script;
        }

        final Script compiled = doCompile( context, source );
        this.cache.put( source.getCacheKey(), compiled );
        return compiled;
    }

    private Script doCompile( final Context context, final ScriptSource source )
    {
        try
        {
            return context.compileString( source.getScriptAsString(), source.getName(), 1, null );
        }
        catch ( final RhinoException e )
        {
            throw new EvalScriptException( source, e );
        }
    }
}
