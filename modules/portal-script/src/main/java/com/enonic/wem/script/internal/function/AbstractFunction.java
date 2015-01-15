package com.enonic.wem.script.internal.function;

import javax.script.Bindings;

import jdk.nashorn.api.scripting.AbstractJSObject;

public abstract class AbstractFunction
    extends AbstractJSObject
{
    @Override
    public final boolean isFunction()
    {
        return true;
    }

    @Override
    public final boolean isStrictFunction()
    {
        return true;
    }

    public abstract void register( final Bindings bindings );
}
