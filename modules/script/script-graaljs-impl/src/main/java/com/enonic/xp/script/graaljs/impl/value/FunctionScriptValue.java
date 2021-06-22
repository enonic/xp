package com.enonic.xp.script.graaljs.impl.value;

import org.graalvm.polyglot.Value;

import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.graaljs.impl.util.JsObjectConverter;

public final class FunctionScriptValue
    extends AbstractScriptValue
{
    private final ScriptValueFactory factory;

    private final Value value;

    public FunctionScriptValue( final ScriptValueFactory factory, final Value value )
    {
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
        JsObjectConverter converter = new JsObjectConverter( factory.getJavascriptHelper() );
        Object[] convertedArgs = converter.toJsArray( args );

        try
        {
            Value result = value.execute( convertedArgs );
            return factory.newValue( result );
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( e );
        }
    }
}
