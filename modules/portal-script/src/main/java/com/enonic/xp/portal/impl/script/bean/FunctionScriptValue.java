package com.enonic.xp.portal.impl.script.bean;

import jdk.nashorn.api.scripting.JSObject;

import com.enonic.xp.script.ScriptValue;

final class FunctionScriptValue
    extends AbstractScriptValue
{
    private final ScriptValueFactory factory;

    private final JSObject value;

    public FunctionScriptValue( final ScriptValueFactory factory, final JSObject value )
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
        final Object result = this.factory.getInvoker().invoke( this.value, args );
        return this.factory.newValue( result );
    }
}
