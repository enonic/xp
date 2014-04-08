package com.enonic.wem.core.script.engine;

import java.io.IOException;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;

import com.enonic.wem.core.module.source.ModuleSource;
import com.enonic.wem.core.script.cache.ScriptCache;

public final class ScriptEngineServiceImpl
    implements ScriptEngineService
{
    private final ScriptCache scriptCache;

    private final ScriptEngine engine;

    private final Compilable compilable;

    @Inject
    public ScriptEngineServiceImpl( final ScriptCache scriptCache )
    {
        this.scriptCache = scriptCache;

        final ScriptEngineManager manager = new ScriptEngineManager();
        this.engine = manager.getEngineByExtension( "js" );
        this.compilable = (Compilable) this.engine;
    }

    @Override
    public Bindings createBindings()
    {
        return this.engine.createBindings();
    }

    @Override
    public ExecutableScript compile( final ModuleSource source )
    {
        final String key = source.getUri() + "_" + source.getTimestamp();
        final CompiledScript compiled = this.scriptCache.get( key, new Callable<CompiledScript>()
        {
            @Override
            public CompiledScript call()
                throws Exception
            {
                return doCompile( source );
            }
        } );

        return new ExecutableScriptImpl( source, compiled );
    }

    private CompiledScript doCompile( final ModuleSource source )
    {
        try
        {
            final String str = source.getBytes().asCharSource( Charsets.UTF_8 ).read();
            return this.compilable.compile( str );
        }
        catch ( final IOException e )
        {
            throw Throwables.propagate( e );
        }
        catch ( final ScriptException e )
        {
            throw ScriptEngineHelper.handleError( source, e );
        }
    }
}
