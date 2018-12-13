package com.enonic.xp.impl.server.rest;

import java.io.IOException;
import java.io.OutputStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.status.StatusReporter;

public class StatusResourceTest
    extends ServerRestTestSupport
{
    private StatusReporter serverReporter;

    @Before
    public void setup()
        throws Exception
    {
    }

    @Override
    protected StatusResource getResourceInstance()
    {
        serverReporter = Mockito.mock( StatusReporter.class );
        Mockito.when( serverReporter.getName() ).thenReturn( "server" );

        final StatusResource resource = new StatusResource();
        resource.setStatusReporter( serverReporter );

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

    @Test(expected = IOException.class)
    public void server_error()
        throws Exception
    {
        Mockito.doThrow( new IOException( "error_message" ) ).when( serverReporter ).report( Mockito.isA( OutputStream.class ) );
        request().path( "status/server" ).get();


    }


}
