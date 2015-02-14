package com.enonic.xp.script.impl;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.SimpleScriptContext;

import com.enonic.wem.api.resource.ResourceKey;

final class ScriptContextImpl
    extends SimpleScriptContext
{
    private final ResourceKey key;

    public ScriptContextImpl( final ResourceKey key )
    {
        this.key = key;
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
            return this.key.toString();
        }

        return super.getAttribute( name );
    }
}
