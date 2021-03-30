package com.enonic.xp.script.impl.value;

import javax.script.Bindings;

import jdk.nashorn.api.scripting.JSObject;

import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.impl.util.JavascriptHelper;
import com.enonic.xp.script.impl.util.NashornHelper;

public final class ScriptValueFactoryImpl
    implements ScriptValueFactory<Bindings>
{
    private final JavascriptHelper<Bindings> helper;

    public ScriptValueFactoryImpl( final JavascriptHelper<Bindings> helper )
    {
        this.helper = helper;
    }

    @Override
    public JavascriptHelper<Bindings> getJavascriptHelper()
    {
        return this.helper;
    }

    @Override
    public ScriptValue evalValue( final String script )
    {
        return newValue( helper.eval( script ) );
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
        if ( NashornHelper.isDateType( value ) )
        {
            return new ScalarScriptValue( NashornHelper.toDate( value ) );
        }

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
