package com.enonic.xp.script.impl.value;

import java.util.List;
import java.util.Map;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.impl.util.JavascriptHelper;

public final class ScriptValueFactoryImpl
    implements ScriptValueFactory
{
    private final JavascriptHelper helper;

    private final Context context;

    public ScriptValueFactoryImpl( final Context context, final JavascriptHelper helper )
    {
        this.helper = helper;
        this.context = context;
    }

    @Override
    public JavascriptHelper getJavascriptHelper()
    {
        return helper;
    }

    @Override
    public ScriptValue newValue( final Object value )
    {
        synchronized ( context )
        {
            if ( value == null )
            {
                return null;
            }

            if ( Value.asValue( value ).isNull() )
            {
                return null;
            }

            if ( value instanceof List )
            {
                return newValue( Value.asValue( value ) );
            }

            if ( value instanceof Map )
            {
                return newValue( Value.asValue( value ) );
            }

            if ( value instanceof Value )
            {
                Value castedValue = (Value) value;
                if ( castedValue.isHostObject() )
                {
                    return new ObjectScriptValue( context, this, castedValue );
                }
                else if ( castedValue.isNumber() )
                {
                    if ( castedValue.fitsInLong() )
                    {
                        return new ScalarScriptValue( context, castedValue.asLong() );
                    }
                    else
                    {
                        return new ScalarScriptValue( context, castedValue.asDouble() );
                    }
//                if ( castedValue.fitsInInt() )
//                {
//                    return new ScalarScriptValue( castedValue.asInt() );
//                }
//                else if ( castedValue.fitsInLong() )
//                {
//                    return new ScalarScriptValue( castedValue.asLong() );
//                }
//                else if ( castedValue.fitsInFloat() )
//                {
//                    return new ScalarScriptValue( castedValue.asFloat() );
//                }
//                else if ( castedValue.fitsInDouble() )
//                {
//                    return new ScalarScriptValue( castedValue.asDouble() );
//                }
//                else if ( castedValue.fitsInByte() )
//                {
//                    return new ScalarScriptValue( castedValue.asByte() );
//                }
//                else
//                {
//                    return new ScalarScriptValue( castedValue.asShort() );
//                }
                }
                else if ( castedValue.isString() )
                {
                    return new ScalarScriptValue( context, castedValue.asString() );
                }
                else if ( castedValue.isBoolean() )
                {
                    return new ScalarScriptValue( context, castedValue.asBoolean() );
                }
                else
                {
                    return newValue( castedValue );
                }
            }

            return new ScalarScriptValue( context, value );
        }
    }

    private ScriptValue newValue( final Value value )
    {
        synchronized ( context )
        {
            if ( value.isDate() )
            {
                return new ScalarScriptValue( context, value.asDate() );
            }

            if ( value.canExecute() )
            {
                return new FunctionScriptValue( context, this, value );
            }

            if ( value.hasArrayElements() )
            {
                return new ArrayScriptValue( context, this, value );
            }

            return new ObjectScriptValue( context, this, value );
        }
    }
}
