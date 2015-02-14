package com.enonic.xp.portal.impl.script;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.xp.portal.script.ScriptExports;
import com.enonic.xp.portal.script.ScriptValue;

final class ScriptExportsImpl
    implements ScriptExports
{
    private final ResourceKey script;

    private final ScriptValue value;

    public ScriptExportsImpl( final ResourceKey script, final ScriptValue value )
    {
        this.script = script;
        this.value = value;
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
}
