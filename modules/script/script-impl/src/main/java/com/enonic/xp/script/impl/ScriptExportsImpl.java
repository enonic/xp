package com.enonic.xp.script.impl;

import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.resource.ResourceKey;

final class ScriptExportsImpl
    implements ScriptExports
{
    private final ResourceKey script;

    private final ScriptValue value;

    private final Object raw;

    public ScriptExportsImpl( final ResourceKey script, final ScriptValue value, final Object raw )
    {
        this.script = script;
        this.value = value;
        this.raw = raw;
    }

    @Override
    public ResourceKey getScript()
    {
        return this.script;
    }

    @Override
    public ScriptValue getValue()
    {
        return this.value;
    }

    @Override
    public boolean hasMethod( final String name )
    {
        return getMethod( name ) != null;
    }

    private ScriptValue getMethod( final String name )
    {
        final ScriptValue func = this.value.getMember( name );
        return ( ( func != null ) && func.isFunction() ) ? func : null;
    }

    @Override
    public ScriptValue executeMethod( final String name, final Object... args )
    {
        final ScriptValue method = getMethod( name );
        if ( method == null )
        {
            return null;
        }

        return method.call( args );
    }

    @Override
    public Object getRawValue()
    {
        return this.raw;
    }
}
