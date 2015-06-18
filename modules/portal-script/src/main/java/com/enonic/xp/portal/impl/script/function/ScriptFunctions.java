package com.enonic.xp.portal.impl.script.function;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.portal.impl.script.ScriptExecutor;
import com.enonic.xp.portal.impl.script.logger.ScriptLogger;
import com.enonic.xp.portal.script.ScriptValue;
import com.enonic.xp.resource.ResourceKey;

public final class ScriptFunctions
{
    private final ResourceKey script;

    private final ScriptExecutor executor;

    public ScriptFunctions( final ResourceKey script, final ScriptExecutor executor )
    {
        this.script = script;
        this.executor = executor;
    }

    public ResourceKey getScript()
    {
        return this.script;
    }

    // TODO: Use ApplicationKey here
    public ModuleKey getApp()
    {
        return this.script.getModule();
    }

    public ModuleKey getModule()
    {
        return this.script.getModule();
    }

    public ScriptLogger getLog()
    {
        return new ScriptLogger( this.script );
    }

    public ExecuteFunction getExecute()
    {
        return new ExecuteFunction( this.script, this.executor );
    }

    public RequireFunction getRequire()
    {
        return new RequireFunction( this.script, this.executor );
    }

    public ResolveFunction getResolve()
    {
        return new ResolveFunction( this.script );
    }

    public Object getBean( final String name )
    {
        return this.executor.getBeanManager().getBean( this.script.getModule(), name );
    }

    public ScriptValue toScriptValue( final Object value )
    {
        return this.executor.newScriptValue( value );
    }
}
