package com.enonic.xp.launcher.impl.weaver;

import java.nio.charset.StandardCharsets;

import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OSGi load-time weaving hook that instruments methods annotated with {@code com.enonic.xp.trace.Traced}.
 * <p>
 * Each annotated method is rewritten when its class is loaded: the original method body is moved to a synthetic
 * method and the annotated method is replaced by a wrapper that executes the body through
 * {@code com.enonic.xp.trace.TraceSupport}, establishing a trace scope around the invocation.
 * <p>
 * Weaving is purely name-based - the hook never loads application classes itself - and applies to every bundle,
 * so both XP internals and third-party applications can use the {@code @Traced} annotation. A bundle that gets
 * woven receives a dynamic import for the {@code com.enonic.xp.trace} package.
 * <p>
 * If a class cannot be woven for any reason it is loaded unmodified, and tracing for that class is simply skipped.
 */
public class TraceWeaver
    implements WeavingHook
{
    static final String TRACE_PACKAGE = "com.enonic.xp.trace";

    private static final Logger LOG = LoggerFactory.getLogger( TraceWeaver.class );

    private static final byte[] TRACED_DESCRIPTOR_BYTES =
        TraceWeaverTransformer.TRACED_DESCRIPTOR.getBytes( StandardCharsets.UTF_8 );

    @Override
    public void weave( final WovenClass wovenClass )
    {
        if ( wovenClass.getClassName().startsWith( TRACE_PACKAGE + "." ) )
        {
            return;
        }

        final byte[] bytes = wovenClass.getBytes();
        if ( !containsTracedDescriptor( bytes ) )
        {
            return;
        }

        try
        {
            final byte[] woven = TraceWeaverTransformer.transform( bytes );
            if ( woven != null )
            {
                wovenClass.setBytes( woven );
                if ( !wovenClass.getDynamicImports().contains( TRACE_PACKAGE ) )
                {
                    wovenClass.getDynamicImports().add( TRACE_PACKAGE );
                }
                LOG.debug( "Woven @Traced methods in {}", wovenClass.getClassName() );
            }
        }
        catch ( final Exception e )
        {
            LOG.error( "Failed to weave @Traced methods in {}. Class is loaded unmodified.", wovenClass.getClassName(), e );
        }
    }

    private static boolean containsTracedDescriptor( final byte[] bytes )
    {
        final byte first = TRACED_DESCRIPTOR_BYTES[0];
        final int max = bytes.length - TRACED_DESCRIPTOR_BYTES.length;

        outer:
        for ( int i = 0; i <= max; i++ )
        {
            if ( bytes[i] != first )
            {
                continue;
            }
            for ( int j = 1; j < TRACED_DESCRIPTOR_BYTES.length; j++ )
            {
                if ( bytes[i + j] != TRACED_DESCRIPTOR_BYTES[j] )
                {
                    continue outer;
                }
            }
            return true;
        }
        return false;
    }
}
