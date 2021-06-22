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
            Value castedValue = (Value) value;
            if ( castedValue.isNumber() )
            {
                if ( castedValue.fitsInInt() )
                {
                    return new ScalarScriptValue( castedValue.asInt() );
                }
                else if ( castedValue.fitsInLong() )
                {
                    return new ScalarScriptValue( castedValue.asLong() );
                }
                else if ( castedValue.fitsInFloat() )
                {
                    return new ScalarScriptValue( castedValue.asFloat() );
                }
                else if ( castedValue.fitsInDouble() )
                {
                    return new ScalarScriptValue( castedValue.asDouble() );
                }
                else if ( castedValue.fitsInByte() )
                {
                    return new ScalarScriptValue( castedValue.asByte() );
                }
                else
                {
                    return new ScalarScriptValue( castedValue.asShort() );
                }
            }
            else if ( castedValue.isString() )
            {
                return new ScalarScriptValue( castedValue.asString() );
            }
            else if ( castedValue.isBoolean() )
            {
                return new ScalarScriptValue( castedValue.asBoolean() );
            }
            else
            {
                return newValue( castedValue );
            }
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
