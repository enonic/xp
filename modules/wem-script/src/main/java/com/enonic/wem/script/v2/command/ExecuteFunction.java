package com.enonic.wem.script.v2.command;

import javax.script.Bindings;

import jdk.nashorn.api.scripting.AbstractJSObject;

public final class ExecuteFunction
    extends AbstractJSObject
{
    @Override
    public boolean isFunction()
    {
        return true;
    }

    @Override
    public boolean isStrictFunction()
    {
        return true;
    }

    public void register( final Bindings bindings )
    {
        bindings.put( "execute", this );
    }
}
