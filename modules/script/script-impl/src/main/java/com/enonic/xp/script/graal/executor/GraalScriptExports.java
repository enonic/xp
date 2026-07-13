package com.enonic.xp.script.graal.executor;

import org.graalvm.polyglot.Value;

import com.enonic.xp.resource.ResourceError;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;

/**
 * Pool-aware exports facade: not bound to one context, it resolves the module's exports in
 * whatever slot serves the invocation (loading the module there on first use). Consumers cache
 * instances of this class (e.g. portal controller scripts), so binding to a single slot would
 * pin all callers of a script to one context and defeat the pool.
 */
final class GraalScriptExports
    implements ScriptExports
{
    private final GraalScriptExecutor executor;

    private final ResourceKey script;

    private final GraalScriptExecutor.ContextSlot pinnedSlot;

    GraalScriptExports( final GraalScriptExecutor executor, final ResourceKey script )
    {
        this( executor, script, null );
    }

    private GraalScriptExports( final GraalScriptExecutor executor, final ResourceKey script,
                                final GraalScriptExecutor.ContextSlot pinnedSlot )
    {
        this.executor = executor;
        this.script = script;
        this.pinnedSlot = pinnedSlot;
    }

    @Override
    public ScriptExports pinned( final Object affinityKey )
    {
        return affinityKey == null ? this : new GraalScriptExports( executor, script, executor.slotFor( affinityKey ) );
    }

    @Override
    public ResourceKey getScript()
    {
        return this.script;
    }

    @Override
    public ScriptValue getValue()
    {
        return executor.withExports( script, pinnedSlot, ( slot, exports ) -> slot.scriptValueFactory.newValue( exports ) );
    }

    @Override
    public boolean hasMethod( final String name )
    {
        return executor.withExports( script, pinnedSlot, ( slot, exports ) -> getMethod( slot, exports, name ) != null );
    }

    @Override
    public ScriptValue executeMethod( final String name, final Object... args )
    {
        return executor.withExports( script, pinnedSlot, ( slot, exports ) -> {
            final ScriptValue method = getMethod( slot, exports, name );
            if ( method == null )
            {
                return null;
            }

            try
            {
                return method.call( args );
            }
            catch ( StackOverflowError e )
            {
                throw new ResourceError( script, "Method execute failed: [" + script + "][" + name + "]", e );
            }
        } );
    }

    @Override
    public Object getRawValue()
    {
        return executor.withExports( script, pinnedSlot, ( slot, exports ) -> exports );
    }

    private ScriptValue getMethod( final GraalScriptExecutor.ContextSlot slot, final Value exports, final String name )
    {
        final ScriptValue value = slot.scriptValueFactory.newValue( exports );
        final ScriptValue func = value.getMember( name );
        return ( ( func != null ) && func.isFunction() ) ? func : null;
    }
}
