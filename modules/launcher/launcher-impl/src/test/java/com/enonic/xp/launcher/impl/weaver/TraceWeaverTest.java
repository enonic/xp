package com.enonic.xp.launcher.impl.weaver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.osgi.framework.hooks.weaving.WovenClass;
import org.osgi.framework.namespace.PackageNamespace;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TraceWeaverTest
{
    @Test
    void weavesClassWithTracedMethods()
        throws Exception
    {
        final byte[] originalBytes = TraceWeaverTransformerTest.fixtureBytes();

        final List<String> dynamicImports = new ArrayList<>();
        final WovenClass wovenClass = mock( WovenClass.class );
        when( wovenClass.getClassName() ).thenReturn( TracedFixture.class.getName() );
        when( wovenClass.getBytes() ).thenReturn( originalBytes );
        when( wovenClass.getDynamicImports() ).thenReturn( dynamicImports );

        new TraceWeaver().weave( wovenClass );

        final ArgumentCaptor<byte[]> captor = ArgumentCaptor.forClass( byte[].class );
        verify( wovenClass ).setBytes( captor.capture() );
        assertThat( captor.getValue() ).isNotEqualTo( originalBytes );
        assertThat( dynamicImports ).containsExactly( "com.enonic.xp.trace" );
    }

    @Test
    void skipsBundlesWithoutTracePackageWiring()
    {
        final BundleWiring wiring = mock( BundleWiring.class, RETURNS_DEEP_STUBS );
        when( wiring.getRequiredWires( anyString() ) ).thenReturn( List.of() );
        when( wiring.getCapabilities( anyString() ) ).thenReturn( List.of() );
        when( wiring.getRevision().getDeclaredRequirements( anyString() ) ).thenReturn( List.of() );

        final WovenClass wovenClass = mock( WovenClass.class );
        when( wovenClass.getClassName() ).thenReturn( TracedFixture.class.getName() );
        when( wovenClass.getBundleWiring() ).thenReturn( wiring );

        new TraceWeaver().weave( wovenClass );

        verify( wovenClass, never() ).getBytes();
        verify( wovenClass, never() ).setBytes( org.mockito.ArgumentMatchers.any() );
    }

    @Test
    void weavesBundlesWiredToTracePackage()
        throws Exception
    {
        final BundleWire traceWire = mock( BundleWire.class, RETURNS_DEEP_STUBS );
        when( traceWire.getCapability().getAttributes() ).thenReturn(
            Map.of( PackageNamespace.PACKAGE_NAMESPACE, "com.enonic.xp.trace" ) );

        final BundleWiring wiring = mock( BundleWiring.class, RETURNS_DEEP_STUBS );
        when( wiring.getRequiredWires( anyString() ) ).thenReturn( List.of( traceWire ) );

        final WovenClass wovenClass = mock( WovenClass.class );
        when( wovenClass.getClassName() ).thenReturn( TracedFixture.class.getName() );
        when( wovenClass.getBundleWiring() ).thenReturn( wiring );
        when( wovenClass.getBytes() ).thenReturn( TraceWeaverTransformerTest.fixtureBytes() );
        when( wovenClass.getDynamicImports() ).thenReturn( new ArrayList<>() );

        new TraceWeaver().weave( wovenClass );

        verify( wovenClass ).setBytes( org.mockito.ArgumentMatchers.any() );
    }

    @Test
    void ignoresClassWithoutTracedAnnotation()
    {
        final WovenClass wovenClass = mock( WovenClass.class );
        when( wovenClass.getClassName() ).thenReturn( WovenClassLoader.class.getName() );
        when( wovenClass.getBytes() ).thenReturn( classBytes( WovenClassLoader.class ) );

        new TraceWeaver().weave( wovenClass );

        verify( wovenClass, never() ).setBytes( org.mockito.ArgumentMatchers.any() );
    }

    @Test
    void ignoresTraceApiPackageItself()
    {
        final WovenClass wovenClass = mock( WovenClass.class );
        when( wovenClass.getClassName() ).thenReturn( "com.enonic.xp.trace.Tracer" );

        new TraceWeaver().weave( wovenClass );

        verify( wovenClass, never() ).getBytes();
        verify( wovenClass, never() ).setBytes( org.mockito.ArgumentMatchers.any() );
    }

    @Test
    void invalidBytesAreLeftUnmodified()
    {
        final byte[] garbage = "Lcom/enonic/xp/trace/Traced;-not-a-classfile".getBytes( java.nio.charset.StandardCharsets.UTF_8 );

        final WovenClass wovenClass = mock( WovenClass.class );
        when( wovenClass.getClassName() ).thenReturn( "com.example.Garbage" );
        when( wovenClass.getBytes() ).thenReturn( garbage );

        new TraceWeaver().weave( wovenClass );

        verify( wovenClass, never() ).setBytes( org.mockito.ArgumentMatchers.any() );
    }

    private static byte[] classBytes( final Class<?> clazz )
    {
        try (var stream = clazz.getResourceAsStream( clazz.getSimpleName() + ".class" ))
        {
            return stream.readAllBytes();
        }
        catch ( final Exception e )
        {
            throw new IllegalStateException( e );
        }
    }
}
