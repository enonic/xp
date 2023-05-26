package com.enonic.xp.script.impl.value;

import javax.script.Bindings;

import org.openjdk.nashorn.api.scripting.JSObject;

import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.impl.util.ErrorHelper;
import com.enonic.xp.script.impl.util.ObjectConverter;

final class FunctionScriptValue
    extends AbstractScriptValue
{
    private final ScriptValueFactory<Bindings> factory;

    private final JSObject value;

    FunctionScriptValue( final ScriptValueFactory<Bindings> factory, final JSObject value )
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
        final ObjectConverter converter = this.factory.getJavascriptHelper().objectConverter();
        final Object[] convertedArgs = converter.toJsArray( args );

        try
        {
            final Object result = this.value.call( this.value, convertedArgs );
            return this.factory.newValue( result );
        }
        catch ( final Exception e )
        {
            throw ErrorHelper.handleError( e );
        }
    }
}
