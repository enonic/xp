package com.enonic.xp.script.impl.executor;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.SimpleScriptContext;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.script.runtime.DebugSettings;
import com.enonic.xp.script.runtime.ScriptSettings;

final class ScriptContextImpl
    extends SimpleScriptContext
{
    private final Resource resource;

    private final DebugSettings debugSettings;

    public ScriptContextImpl( final Resource resource, final ScriptSettings settings )
    {
        this.resource = resource;
        this.debugSettings = settings.getDebug();
    }

    public void setEngineScope( final Bindings scope )
    {
        setBindings( scope, ENGINE_SCOPE );
    }

    public void setGlobalScope( final Bindings scope )
    {
        setBindings( scope, GLOBAL_SCOPE );
    }

    @Override
    public Object getAttribute( final String name )
    {
        if ( name.equals( ScriptEngine.FILENAME ) )
        {
            return getFileName();
        }

        return super.getAttribute( name );
    }

    private String getFileName()
    {
        if ( this.debugSettings != null )
        {
            return this.debugSettings.scriptName( this.resource );
        }

        return this.resource.getKey().toString();
    }
}
