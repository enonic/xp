package com.enonic.xp.script.impl.value;

import jdk.nashorn.api.scripting.JSObject;

import com.enonic.xp.portal.impl.script.util.NashornHelper;
import com.enonic.xp.script.ScriptValue;

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

        if ( NashornHelper.isUndefined( value ) )
        {
            return null;
        }

        if ( value instanceof JSObject )
        {
            return newValue( (JSObject) value );
        }

        return new ScalarScriptValue( value );
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
