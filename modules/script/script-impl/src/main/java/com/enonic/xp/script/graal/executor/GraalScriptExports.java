package com.enonic.xp.script.graal.executor;

import org.graalvm.polyglot.Context;

import com.enonic.xp.resource.ResourceError;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;

final class GraalScriptExports
    implements ScriptExports
{
    private final Context context;

    private final ResourceKey script;

    private final ScriptValue value;

    private final Object raw;

    GraalScriptExports( final Context context, final ResourceKey script, final ScriptValue value, final Object raw )
    {
        this.context = context;
        this.script = script;
        this.value = value;
        this.raw = raw;
    }

    @Override
    public ResourceKey getScript()
    {
        return this.script;
    }

    @Override
    public ScriptValue getValue()
    {
        return this.value;
    }

    @Override
    public boolean hasMethod( final String name )
    {
        return getMethod( name ) != null;
    }

    @Override
    public ScriptValue executeMethod( final String name, final Object... args )
    {
        final ScriptValue method = getMethod( name );
        if ( method == null )
        {
            return null;
        }

        synchronized ( context )
        {
            try
            {
                return method.call( args );
            }
            catch ( StackOverflowError e )
            {
                throw new ResourceError( script, "Method execute failed: [" + script + "][" + name + "]", e );
            }
        }
    }

    @Override
    public Object getRawValue()
    {
        return this.raw;
    }

    private ScriptValue getMethod( final String name )
    {
        synchronized ( context )
        {
            final ScriptValue func = this.value.getMember( name );
            return ( ( func != null ) && func.isFunction() ) ? func : null;
        }
    }
}
