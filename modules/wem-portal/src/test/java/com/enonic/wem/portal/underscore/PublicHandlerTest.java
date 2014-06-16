package com.enonic.wem.portal.underscore;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;

import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.portal.base.ModuleBaseHandlerTest;

import static org.junit.Assert.*;

public class PublicHandlerTest
    extends ModuleBaseHandlerTest<PublicHandler>
{
    @Override
    protected PublicHandler createResource()
    {
        return new PublicHandler();
    }

    @Before
    public void setup()
        throws Exception
    {
        super.setup();
    }

    @Test
    public void getPublicResource_found()
        throws Exception
    {
        addResource( "main.css", "demo-1.0.0:/public/css/main.css", "p {color:red;}" );

        final ClientResponse response = executeGet( "/live/path/to/content/_/public/demo-1.0.0/css/main.css" );
        assertEquals( 200, response.getStatus() );
        assertEquals( "text/css", response.getType().toString() );
        assertEquals( "p {color:red;}", response.getEntity( String.class ) );
    }

    @Test(expected = ModuleNotFoundException.class)
    public void getPublicResource_moduleNotFound()
        throws Exception
    {
        executeGet( "/live/path/to/content/_/public/demo/css/main.css" );
    }

    @Test
    public void getPublicResource_fileNotFound()
        throws Exception
    {
        final ClientResponse response = executeGet( "/live/path/to/content/_/public/demo-1.0.0/css/main.css" );
        assertEquals( 404, response.getStatus() );
    }

    @Test
    public void methodNotAllowed()
        throws Exception
    {
        final ClientResponse response = executePost( "/live/path/to/content/_/public/demo-1.0.0/css/main.css" );
        assertEquals( 405, response.getStatus() );
    }
}
