package com.enonic.xp.launcher.impl.weaver;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.osgi.framework.hooks.weaving.WovenClass;
import org.osgi.framework.startlevel.BundleStartLevel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NashornWeaverTest
{

    @Test
    void weave()
        throws Exception
    {
        final WovenClass wovenClass = mock( WovenClass.class, RETURNS_DEEP_STUBS );
        final BundleStartLevel bundleStartLevelMock = mock( BundleStartLevel.class );
        when( wovenClass.getBundleWiring().getBundle().adapt( same( BundleStartLevel.class ) ) ).thenReturn( bundleStartLevelMock );
        when( bundleStartLevelMock.getStartLevel() ).thenReturn( 41 );

        final byte[] originalBytes = readResource();
        // Precondition: make sure that the class contains the original jdk/nashorn
        assertThat( originalBytes ).containsSequence( "Ljdk/nashorn/api/scripting/JSObject".getBytes( StandardCharsets.UTF_8 ) );

        when( wovenClass.getBytes() ).thenReturn( originalBytes );
        new NashornWeaver( 40 ).weave( wovenClass );

        final ArgumentCaptor<byte[]> captor = ArgumentCaptor.forClass( byte[].class );
        verify( wovenClass ).setBytes( captor.capture() );

        assertThat( captor.getValue() ).containsSequence(
            "Lorg/openjdk/nashorn/api/scripting/JSObject".getBytes( StandardCharsets.UTF_8 ) );
    }

    private byte[] readResource()
        throws IOException
    {
        try (InputStream stream = this.getClass().getResourceAsStream( "/WithJdkNashorn.clazz" ))
        {
            assert stream != null;
            return stream.readAllBytes();
        }
    }
}
