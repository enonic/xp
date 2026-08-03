package com.enonic.xp.script.graal.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

/**
 * The only way a JS function may cross the Java boundary. A GraalJS {@link Value} is bound to its
 * {@link Context}, and a context permits one thread inside it at a time — so a raw function proxy
 * handed to another thread (task, event dispatch, websocket) fails with a multi-threaded access
 * error. The handle routes every invocation through the context's ownership discipline (currently
 * the context monitor; a worker queue once context pooling lands) regardless of the calling thread.
 * <p>
 * Results are converted eagerly to plain Java values so no context-bound {@link Value} escapes
 * without a handle; nested functions become nested handles.
 * <p>
 * The interfaces below are the ones a bean may declare for a parameter it wants a script function
 * for. A functional interface outside this set is rejected at the boundary rather than satisfied
 * by a proxy the handle does not control — see {@code GraalJSContextFactory}.
 */
public final class JsFunctionHandle
    implements Function<Object, Object>, UnaryOperator<Object>, Consumer<Object>, BiConsumer<Object, Object>, Runnable,
    Supplier<Object>, Callable<Object>, Predicate<Object>, Comparator<Object>
{
    private static final Object[] NO_ARGS = new Object[0];

    private final Context context;

    private final Value function;

    public JsFunctionHandle( final Context context, final Value function )
    {
        this.context = context;
        this.function = function;
    }

    public Object execute( final Object... args )
    {
        synchronized ( context )
        {
            try
            {
                return convert( function.execute( args ) );
            }
            catch ( final Exception e )
            {
                throw GraalErrorHelper.handleError( e );
            }
        }
    }

    @Override
    public Object apply( final Object arg )
    {
        // preserves the historic ObjectConverter.fromJs contract Function<Object[], Object>,
        // where the argument is the full argument array
        return arg instanceof Object[] ? execute( (Object[]) arg ) : execute( arg );
    }

    @Override
    public void accept( final Object arg )
    {
        execute( arg );
    }

    @Override
    public void accept( final Object first, final Object second )
    {
        execute( first, second );
    }

    @Override
    public void run()
    {
        execute( NO_ARGS );
    }

    @Override
    public Object get()
    {
        return execute( NO_ARGS );
    }

    @Override
    public Object call()
    {
        return execute( NO_ARGS );
    }

    @Override
    public boolean test( final Object arg )
    {
        return Boolean.TRUE.equals( execute( arg ) );
    }

    @Override
    public int compare( final Object first, final Object second )
    {
        final Object result = execute( first, second );
        if ( !( result instanceof Number ) )
        {
            throw new IllegalArgumentException( "Comparator function must return a number, got " + result );
        }
        return ( (Number) result ).intValue();
    }

    /**
     * The two-argument interfaces cannot sit on the handle itself: {@code BiFunction.andThen} and
     * {@code BiPredicate.negate} have the same erasure as their one-argument counterparts but a
     * different return type, so a single class cannot implement both. This shares the handle's
     * routing and conversion.
     */
    public static final class TwoArg
        implements BiFunction<Object, Object, Object>, BinaryOperator<Object>, BiPredicate<Object, Object>
    {
        private final JsFunctionHandle handle;

        public TwoArg( final JsFunctionHandle handle )
        {
            this.handle = handle;
        }

        @Override
        public Object apply( final Object first, final Object second )
        {
            return handle.execute( first, second );
        }

        @Override
        public boolean test( final Object first, final Object second )
        {
            return Boolean.TRUE.equals( handle.execute( first, second ) );
        }
    }

    private Object convert( final Value value )
    {
        if ( value == null || value.isNull() )
        {
            return null;
        }
        else if ( value.isHostObject() )
        {
            return value.asHostObject();
        }
        else if ( value.canExecute() )
        {
            return new JsFunctionHandle( context, value );
        }
        else if ( GraalJSHelper.isDateType( value ) )
        {
            return GraalJSHelper.toDate( value );
        }
        else if ( value.isNumber() )
        {
            return value.as( Number.class );
        }
        else if ( value.isString() )
        {
            return value.asString();
        }
        else if ( value.isBoolean() )
        {
            return value.asBoolean();
        }
        else if ( value.hasArrayElements() )
        {
            final List<Object> result = new ArrayList<>();
            for ( int i = 0; i < value.getArraySize(); i++ )
            {
                final Object converted = convert( value.getArrayElement( i ) );
                // null elements are dropped, matching GraalObjectConverter: the same JS value must
                // convert identically whether it crosses via fromJs or a handle's return value
                if ( converted != null )
                {
                    result.add( converted );
                }
            }
            return result;
        }
        else
        {
            final Map<String, Object> result = new LinkedHashMap<>();
            for ( final String key : value.getMemberKeys() )
            {
                // null-valued keys stay, matching GraalObjectConverter (and Nashorn):
                // {key: null} is not {}
                result.put( key, convert( value.getMember( key ) ) );
            }
            return result;
        }
    }
}
