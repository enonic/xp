package com.enonic.xp.script.impl.value;

import jdk.nashorn.api.scripting.JSObject;

import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.impl.util.ErrorHelper;
import com.enonic.xp.script.impl.util.JsObjectConverter;

final class FunctionScriptValue
    extends AbstractScriptValue
{
    private final ScriptValueFactory factory;

    private final JSObject value;

    FunctionScriptValue( final ScriptValueFactory factory, final JSObject value )
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
        final JsObjectConverter converter = new JsObjectConverter( this.factory.getJavascriptHelper() );
        final Object[] convertedArgs = converter.toJsArray( args );

        try
        {
            final Object result = this.value.call( null, convertedArgs );
            return this.factory.newValue( result );
        }
        catch ( final Exception e )
        {
            throw ErrorHelper.handleError( e );
        }
    }
}
