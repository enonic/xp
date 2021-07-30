package com.enonic.xp.script.impl.function;

import org.graalvm.polyglot.proxy.ProxyExecutable;

public abstract class AbstractFunction
    implements ProxyExecutable
{
    private final String name;

    protected AbstractFunction( final String name )
    {
        this.name = name;
    }
}
