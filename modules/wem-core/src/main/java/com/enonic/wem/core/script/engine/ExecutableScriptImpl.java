package com.enonic.wem.core.script.engine;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptException;

import com.enonic.wem.core.module.source.ModuleSource;

final class ExecutableScriptImpl
    implements ExecutableScript
{
    private final ModuleSource source;

    private final CompiledScript script;

    public ExecutableScriptImpl( final ModuleSource source, final CompiledScript script )
    {
        this.source = source;
        this.script = script;
    }

    @Override
    public ModuleSource getSource()
    {
        return this.source;
    }

    @Override
    public CompiledScript getScript()
    {
        return this.script;
    }

    @Override
    public Object execute( final Bindings bindings )
    {
        try
        {
            return this.script.eval( bindings );
        }
        catch ( final ScriptException e )
        {
            throw ScriptEngineHelper.handleError( this.source, e );
        }
    }
}
