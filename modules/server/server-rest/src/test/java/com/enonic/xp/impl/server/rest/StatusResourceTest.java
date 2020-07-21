package com.enonic.xp.impl.server.rest;

import java.io.IOException;
import java.io.OutputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.common.net.MediaType;

import com.enonic.xp.status.StatusReporter;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class StatusResourceTest
    extends ServerRestTestSupport
{
    private StatusReporter serverReporter;

    @BeforeEach
    public void setup()
        throws Exception
    {
    }

    @Override
    protected StatusResource getResourceInstance()
    {
        serverReporter = Mockito.mock( StatusReporter.class );
        when( serverReporter.getName() ).thenReturn( "server" );

        final StatusResource resource = new StatusResource();
        resource.addStatusReporter( serverReporter );
        when( serverReporter.getMediaType() ).thenReturn( MediaType.PLAIN_TEXT_UTF_8 );
        return resource;
    }

    @Test
    public void server()
        throws Exception
    {
        final ArgumentCaptor<OutputStream> commentCaptor = ArgumentCaptor.forClass( OutputStream.class );

        request().path( "status/server" ).get();

        Mockito.verify( serverReporter ).report( commentCaptor.capture() );
        Mockito.verify( serverReporter, Mockito.times( 1 ) ).report( Mockito.any( OutputStream.class ) );
    }

    @Test
    public void server_error()
        throws Exception
    {
        Mockito.doThrow( new IOException( "error_message" ) ).when( serverReporter ).report( Mockito.isA( OutputStream.class ) );

        assertThrows( IOException.class, () -> request().path( "status/server" ).get() );
    }
}
