package com.enonic.wem.core.script;

import java.util.Stack;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.core.script.resolver.ScriptResolver;
import com.enonic.wem.core.script.resolver.ScriptResolverImpl;

public final class ScriptContext
{
    private final static ThreadLocal<ScriptContext> CONTEXT = new ThreadLocal<>();

    private final Stack<ResourceKey> resourceKey;

    public ScriptContext()
    {
        this.resourceKey = new Stack<>();
    }

    public ScriptResolver getResolver()
    {
        return new ScriptResolverImpl( this.resourceKey.peek(), null );
    }

    public void enter( final ResourceKey resourceKey )
    {
        CONTEXT.set( this );
        this.resourceKey.push( resourceKey );
    }

    public void exit()
    {
        this.resourceKey.pop();
        if ( this.resourceKey.isEmpty() )
        {
            CONTEXT.remove();
        }
    }

    public static ScriptContext current()
    {
        return CONTEXT.get();
    }
}
