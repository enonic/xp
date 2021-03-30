package com.enonic.xp.script.graal.function;

import org.graalvm.polyglot.proxy.ProxyExecutable;

public abstract class GraalAbstractFunction
    implements ProxyExecutable
{
    private final String name;

    protected GraalAbstractFunction( final String name )
    {
        this.name = name;
    }
}
