package com.enonic.wem.script.v2.command;

import javax.script.Bindings;

import jdk.nashorn.api.scripting.AbstractJSObject;

public final class CommandFunction
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
        bindings.put( "command", this );
    }
}
