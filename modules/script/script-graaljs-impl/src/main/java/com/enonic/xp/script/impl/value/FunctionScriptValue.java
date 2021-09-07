package com.enonic.xp.script.impl.value;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.impl.util.ErrorHelper;
import com.enonic.xp.script.impl.util.JsObjectConverter;

final class FunctionScriptValue
    extends AbstractScriptValue
{
    private final Context context;

    private final ScriptValueFactory factory;

    private final Value value;

    public FunctionScriptValue( final Context context, final ScriptValueFactory factory, final Value value )
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
            JsObjectConverter converter = new JsObjectConverter( factory.getJavascriptHelper() );
            Object[] convertedArgs = converter.toJsArray( args );

            try
            {
                Value result = value.execute( convertedArgs );
                return factory.newValue( result );
            }
            catch ( final Exception e )
            {
                throw ErrorHelper.handleError( e );
            }
        }
    }
}
