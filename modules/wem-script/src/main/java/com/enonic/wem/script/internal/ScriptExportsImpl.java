package com.enonic.wem.script.internal;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.script.ScriptExports;
import com.enonic.wem.script.ScriptObject;

final class ScriptExportsImpl
    implements ScriptExports
{
    private final ResourceKey script;

    private final ScriptObject value;

    public ScriptExportsImpl( final ResourceKey script, final ScriptObject value )
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
    public ScriptObject getValue()
    {
        return this.value;
    }

    @Override
    public boolean hasMethod( final String name )
    {
        return getMethod( name ) != null;
    }

    private ScriptObject getMethod( final String name )
    {
        final ScriptObject func = this.value.getMember( name );
        return ( ( func != null ) && func.isFunction() ) ? func : null;
    }

    @Override
    public ScriptObject executeMethod( final String name, final Object... args )
    {
        final ScriptObject method = getMethod( name );
        if ( method == null )
        {
            return null;
        }

        return method.call( args );
    }
}
