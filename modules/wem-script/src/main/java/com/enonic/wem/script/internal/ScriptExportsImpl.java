package com.enonic.wem.script.internal;

import javax.script.Bindings;

import jdk.nashorn.api.scripting.JSObject;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.script.ScriptExports;
import com.enonic.wem.script.ScriptObject;

final class ScriptExportsImpl
    implements ScriptExports
{
    private final ResourceKey script;

    private final ScriptExecutor executor;

    private final Bindings bindings;

    public ScriptExportsImpl( final ResourceKey script, final ScriptExecutor executor, final Bindings bindings )
    {
        this.script = script;
        this.executor = executor;
        this.bindings = bindings;
    }

    @Override
    public ResourceKey getScript()
    {
        return this.script;
    }

    @Override
    public boolean hasMethod( final String name )
    {
        return getMethod( name ) != null;
    }

    private JSObject getMethod( final String name )
    {
        if ( this.bindings == null )
        {
            return null;
        }

        final Object result = this.bindings.get( name );
        if ( !( result instanceof JSObject ) )
        {
            return null;
        }

        final JSObject jsObject = (JSObject) result;
        if ( jsObject.isFunction() )
        {
            return jsObject;
        }

        return null;
    }

    @Override
    public ScriptObject executeMethod( final String name, final Object... args )
    {
        final JSObject method = getMethod( name );
        if ( method == null )
        {
            return null;
        }

        return this.executor.invokeMethod( this.bindings, method, args );
    }
}
