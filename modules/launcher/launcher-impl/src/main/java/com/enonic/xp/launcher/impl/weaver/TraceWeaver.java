package com.enonic.xp.launcher.impl.weaver;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;
import org.osgi.framework.namespace.PackageNamespace;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.resource.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OSGi load-time weaving hook that instruments methods annotated with {@code com.enonic.xp.trace.Traced}.
 * <p>
 * Each annotated method is rewritten when its class is loaded: the original method body is moved to a synthetic
 * method and the annotated method is replaced by a wrapper that executes the body through
 * {@code com.enonic.xp.trace.TraceSupport}, establishing a trace scope around the invocation.
 * <p>
 * Weaving is purely name-based - the hook never loads application classes itself - and applies to every bundle
 * that can see the {@code com.enonic.xp.trace} package, so both XP internals and third-party applications can use
 * the {@code @Traced} annotation. Bundles without any wiring to that package are skipped without inspecting their
 * class bytes. A bundle that gets woven receives a dynamic import for the package.
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

    private final Map<BundleWiring, Boolean> tracePackageVisible = Collections.synchronizedMap( new WeakHashMap<>() );

    @Override
    public void weave( final WovenClass wovenClass )
    {
        if ( wovenClass.getClassName().startsWith( TRACE_PACKAGE + "." ) )
        {
            return;
        }

        if ( !canSeeTracePackage( wovenClass.getBundleWiring() ) )
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
        catch ( final Throwable t )
        {
            // Never let anything escape: a throwing WeavingHook fails the class load and gets blacklisted by the
            // framework, silently disabling tracing for the rest of the server lifetime.
            LOG.error( "Failed to weave @Traced methods in {}. Class is loaded unmodified.", wovenClass.getClassName(), t );
        }
    }

    /**
     * A class can only reference the {@code @Traced} annotation if its bundle can see the trace package: through a
     * resolved package wire, by providing the package itself, or through a dynamic package import. Everything else
     * (framework internals, third-party libraries) is skipped without scanning class bytes.
     */
    private boolean canSeeTracePackage( final BundleWiring wiring )
    {
        if ( wiring == null )
        {
            return true;
        }

        return this.tracePackageVisible.computeIfAbsent( wiring, TraceWeaver::computeTracePackageVisible );
    }

    private static Boolean computeTracePackageVisible( final BundleWiring wiring )
    {
        for ( final BundleWire wire : wiring.getRequiredWires( PackageNamespace.PACKAGE_NAMESPACE ) )
        {
            if ( TRACE_PACKAGE.equals( wire.getCapability().getAttributes().get( PackageNamespace.PACKAGE_NAMESPACE ) ) )
            {
                return true;
            }
        }

        for ( final BundleCapability capability : wiring.getCapabilities( PackageNamespace.PACKAGE_NAMESPACE ) )
        {
            if ( TRACE_PACKAGE.equals( capability.getAttributes().get( PackageNamespace.PACKAGE_NAMESPACE ) ) )
            {
                return true;
            }
        }

        // DynamicImport-Package filters can be wildcards - do not try to prove the package unreachable.
        for ( final BundleRequirement requirement : wiring.getRevision().getDeclaredRequirements( PackageNamespace.PACKAGE_NAMESPACE ) )
        {
            if ( PackageNamespace.RESOLUTION_DYNAMIC.equals(
                requirement.getDirectives().get( Namespace.REQUIREMENT_RESOLUTION_DIRECTIVE ) ) )
            {
                return true;
            }
        }

        return false;
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
