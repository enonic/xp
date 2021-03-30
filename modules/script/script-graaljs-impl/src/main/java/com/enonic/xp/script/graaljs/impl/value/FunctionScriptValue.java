package com.enonic.xp.script.graaljs.impl.value;

import org.graalvm.polyglot.Value;

import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.graaljs.impl.util.JsObjectConverter;

public final class FunctionScriptValue
    extends AbstractScriptValue
{
    private static final ScriptValueFactory FACTORY = new ScriptValueFactoryImpl();

    private final Value value;

    public FunctionScriptValue( final Value value )
    {
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
        final JsObjectConverter converter = new JsObjectConverter();
        Value result = value.execute( args );
        return FACTORY.newValue( result );
    }
}
