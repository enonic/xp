package com.enonic.xp.script.impl.function;

import javax.script.Bindings;

import jdk.nashorn.api.scripting.JSObject;

import com.enonic.xp.script.impl.bean.JsObjectConverter;

public final class CallFunction
    extends AbstractFunction
{
    @Override
    public Object call( final Object thiz, final Object... args )
    {
        final JSObject func = (JSObject) args[0];
        final Object[] array = (Object[]) args[1];

        final Object[] jsArray = JsObjectConverter.toJsArray( array );
        return func.call( thiz, jsArray );
    }

    @Override
    public void register( final Bindings bindings )
    {
        bindings.put( "__call", this );
    }
}
