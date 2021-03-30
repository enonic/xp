package com.enonic.xp.script.graal.value;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.graal.util.GraalErrorHelper;
import com.enonic.xp.script.impl.util.ObjectConverter;
import com.enonic.xp.script.impl.value.AbstractScriptValue;
import com.enonic.xp.script.impl.value.ScriptValueFactory;

final class GraalFunctionScriptValue
    extends AbstractScriptValue
{
    private final Context context;

    private final ScriptValueFactory<Value> factory;

    private final Value value;

    GraalFunctionScriptValue( final Context context, final ScriptValueFactory<Value> factory, final Value value )
    {
        this.context = context;
        this.factory = factory;
        this.value = value;
    }

    @Override
    public boolean isFunction()
    {
        return true;
    }

    @Override
    public ScriptValue call( final Object... args )
    {
        synchronized ( context )
        {
            ObjectConverter converter = factory.getJavascriptHelper().objectConverter();
            Object[] convertedArgs = converter.toJsArray( args );

            try
            {
                Value result = value.execute( convertedArgs );
                return factory.newValue( result );
            }
            catch ( final Exception e )
            {
                throw GraalErrorHelper.handleError( e );
            }
        }
    }
}
