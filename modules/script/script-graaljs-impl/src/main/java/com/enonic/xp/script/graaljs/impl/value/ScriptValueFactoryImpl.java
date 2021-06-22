package com.enonic.xp.script.graaljs.impl.value;

import org.graalvm.polyglot.Value;

import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.graaljs.impl.util.JavascriptHelper;

public final class ScriptValueFactoryImpl
    implements ScriptValueFactory
{
    private final JavascriptHelper helper;

    public ScriptValueFactoryImpl( final JavascriptHelper helper )
    {
        this.helper = helper;
    }

    @Override
    public JavascriptHelper getJavascriptHelper()
    {
        return helper;
    }

    @Override
    public ScriptValue newValue( final Object value )
    {
        if ( value == null )
        {
            return null;
        }

        if ( Value.asValue( value ).isNull() )
        {
            return null;
        }

        if ( value instanceof Value )
        {
            return newValue( Value.asValue( value ) );
        }

        return new ScalarScriptValue( value );
    }

    private ScriptValue newValue( final Value value )
    {
        if ( value.isDate() )
        {
            return new ScalarScriptValue( value.asDate() );
        }

        if ( value.canExecute() )
        {
            return new FunctionScriptValue( this, value );
        }

        if ( value.hasArrayElements() )
        {
            return new ArrayScriptValue( this, value );
        }

        return new ObjectScriptValue( this, value );
    }
}
