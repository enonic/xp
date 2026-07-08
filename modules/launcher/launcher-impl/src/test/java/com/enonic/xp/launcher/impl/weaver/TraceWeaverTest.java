package com.enonic.xp.launcher.impl.weaver;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.osgi.framework.hooks.weaving.WovenClass;

import static org.assertj.core.api.Assertions.assertThat;
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
