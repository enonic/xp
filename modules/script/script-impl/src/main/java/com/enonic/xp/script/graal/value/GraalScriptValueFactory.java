package com.enonic.xp.script.graal.value;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.graal.GraalJSContextFactory;
import com.enonic.xp.script.graal.util.GraalJavascriptHelperFactory;
import com.enonic.xp.script.impl.util.JavascriptHelper;
import com.enonic.xp.script.impl.value.ScriptValueFactory;

public final class GraalScriptValueFactory
    implements ScriptValueFactory<Value>, Closeable
{
    private final JavascriptHelper<Value> helper;

    private final Context context;

    public GraalScriptValueFactory( final GraalJSContextFactory contextFactory, final GraalJavascriptHelperFactory helper )
    {
        this.context = contextFactory.create();
        this.helper = helper.create( context );
    }

    @Override
    public JavascriptHelper<Value> getJavascriptHelper()
    {
        return helper;
    }

    public Context getContext()
    {
        return context;
    }

    @Override
    public void close()
        throws IOException
    {
        context.close();
    }

    @Override
    public ScriptValue evalValue( final String script )
    {
        return newValue( helper.eval( script ) );
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
                    return new GraalObjectScriptValue( context, this, castedValue );
                }
                else if ( castedValue.isNumber() )
                {
                    return new GraalScalarScriptValue( context, castedValue.as( Number.class ) );
                }
                else if ( castedValue.isString() )
                {
                    return new GraalScalarScriptValue( context, castedValue.asString() );
                }
                else if ( castedValue.isBoolean() )
                {
                    return new GraalScalarScriptValue( context, castedValue.asBoolean() );
                }
                else
                {
                    return newValue( castedValue );
                }
            }

            return new GraalScalarScriptValue( context, value );
        }
    }

    private ScriptValue newValue( final Value value )
    {
        if ( value.isDate() )
        {
            return new GraalScalarScriptValue( context, value.asDate() );
        }

        if ( value.canExecute() )
        {
            return new GraalFunctionScriptValue( context, this, value );
        }

        if ( value.hasArrayElements() )
        {
            return new GraalArrayScriptValue( context, this, value );
        }

        return new GraalObjectScriptValue( context, this, value );
    }
}
