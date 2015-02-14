package com.enonic.wem.script.internal.bean;

import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.ScriptUtils;
import jdk.nashorn.internal.runtime.Undefined;

import com.enonic.xp.portal.script.ScriptValue;

public final class ScriptValueFactoryImpl
    implements ScriptValueFactory
{
    private final ScriptMethodInvoker invoker;

    public ScriptValueFactoryImpl( final ScriptMethodInvoker invoker )
    {
        this.invoker = invoker;
    }

    @Override
    public ScriptMethodInvoker getInvoker()
    {
        return this.invoker;
    }

    @Override
    public ScriptValue newValue( final Object value )
    {
        if ( value == null )
        {
            return null;
        }

        final Object unwrapped = ScriptUtils.unwrap( value );
        if ( unwrapped instanceof Undefined )
        {
            return null;
        }

        if ( unwrapped instanceof JSObject )
        {
            return newValue( (JSObject) unwrapped );
        }

        return new ScalarScriptValue( unwrapped );
    }

    private ScriptValue newValue( final JSObject value )
    {
        if ( value.isFunction() )
        {
            return new FunctionScriptValue( this, value );
        }

        if ( value.isArray() )
        {
            return new ArrayScriptValue( this, value );
        }

        return new ObjectScriptValue( this, value );
    }
}
