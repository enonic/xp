package com.enonic.xp.script.graal.executor;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

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

    private final boolean isolated;

    /**
     * References this view holds on its pinned slot. A slot can be pinned by several views
     * (connections whose handshakes shared it), so a view must never release more than it
     * acquired — an endpoint releases on any open failure, including a {@link #retain()} that
     * threw on the exhausted connection budget, and that release must not strip another
     * connection's pin.
     */
    private final AtomicInteger retained = new AtomicInteger();

    GraalScriptExports( final GraalScriptExecutor executor, final ResourceKey script )
    {
        this( executor, script, null, false );
    }

    /**
     * A view permanently pinned to the given slot — used for {@code main.js}, whose exports
     * must always execute in the dedicated main context.
     */
    static GraalScriptExports pinnedTo( final GraalScriptExecutor executor, final ResourceKey script,
                                        final GraalScriptExecutor.ContextSlot slot )
    {
        return new GraalScriptExports( executor, script, slot, false );
    }

    /**
     * An isolated view bound to no context: each invocation runs in a fresh private context where
     * the script's top level executes lazily — what {@code executeMethod} returns, obtainable
     * without executing the script on a pooled slot first.
     */
    static GraalScriptExports isolated( final GraalScriptExecutor executor, final ResourceKey script )
    {
        return new GraalScriptExports( executor, script, null, true );
    }

    private GraalScriptExports( final GraalScriptExecutor executor, final ResourceKey script,
                                final GraalScriptExecutor.ContextSlot pinnedSlot, final boolean isolated )
    {
        this.executor = executor;
        this.script = script;
        this.pinnedSlot = pinnedSlot;
        this.isolated = isolated;
    }

    @Override
    public <T> T executeBound( final Function<ScriptExports, T> work )
    {
        if ( isolated )
        {
            return work.apply( this );
        }
        // resolve one slot up front (honoring an existing binding or pin) and keep it bound for
        // the whole scope; the view handed to work stays pinned to that exact slot afterwards
        return executor.withSlot( pinnedSlot, slot -> work.apply( new GraalScriptExports( executor, script, slot, false ) ) );
    }

    @Override
    public void retain()
    {
        if ( pinnedSlot != null )
        {
            pinnedSlot.retain();
            retained.incrementAndGet();
        }
    }

    @Override
    public void release()
    {
        if ( pinnedSlot != null && retained.getAndUpdate( count -> Math.max( 0, count - 1 ) ) > 0 )
        {
            pinnedSlot.release();
        }
    }

    @Override
    public ResourceKey getScript()
    {
        return this.script;
    }

    @Override
    public ScriptValue getValue()
    {
        return withExports( ( slot, exports ) -> slot.scriptValueFactory.newValue( exports ) );
    }

    @Override
    public boolean hasMethod( final String name )
    {
        return withExports( ( slot, exports ) -> getMethod( slot, exports, name ) != null );
    }

    @Override
    public ScriptValue executeMethod( final String name, final Object... args )
    {
        return withExports( ( slot, exports ) -> {
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
        return withExports( ( slot, exports ) -> exports );
    }

    /**
     * Isolated invocation: the method must exist — {@link #executeMethod} would answer a
     * missing method with an indistinguishable {@code null}. One private context per call,
     * method lookup and execution inside it; the result comes back only when it is a scalar
     * (unboxed eagerly, so it survives the context's close) — richer values would not, and are
     * not (yet) converted.
     */
    Object executeMethodRequired( final String name, final Object... args )
    {
        return withExports( ( slot, exports ) -> {
            final ScriptValue method = getMethod( slot, exports, name );
            if ( method == null )
            {
                throw new IllegalArgumentException( "Method [" + name + "] not found in script [" + script + "]" );
            }
            try
            {
                final ScriptValue result = method.call( args );
                return result != null && result.isValue() ? result.getValue() : null;
            }
            catch ( StackOverflowError e )
            {
                throw new ResourceError( script, "Method execute failed: [" + script + "][" + name + "]", e );
            }
        } );
    }

    private <T> T withExports( final BiFunction<GraalScriptExecutor.ContextSlot, Value, T> work )
    {
        return isolated ? executor.withIsolatedExports( script, work ) : executor.withExports( script, pinnedSlot, work );
    }

    private ScriptValue getMethod( final GraalScriptExecutor.ContextSlot slot, final Value exports, final String name )
    {
        final ScriptValue value = slot.scriptValueFactory.newValue( exports );
        final ScriptValue func = value.getMember( name );
        return ( ( func != null ) && func.isFunction() ) ? func : null;
    }
}
