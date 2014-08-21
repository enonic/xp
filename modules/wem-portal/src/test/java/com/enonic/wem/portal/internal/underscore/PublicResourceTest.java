package com.enonic.wem.portal.internal.underscore;

import org.junit.Test;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;

import com.enonic.wem.portal.internal.base.ModuleBaseResourceTest;

import static org.junit.Assert.*;

public class PublicResourceTest
    extends ModuleBaseResourceTest<PublicResource>
{
    @Override
    protected void configure()
        throws Exception
    {
        this.resource = new PublicResource();
        super.configure();
    }

    @Test
    public void getPublicResource_found()
        throws Exception
    {
        addResource( "main.css", "demo-1.0.0:/public/css/main.css", "p {color:red;}" );

        final Request request = new Request( Method.GET, "/live/path/to/content/_/public/demo-1.0.0/css/main.css" );
        final Response response = executeRequest( request );

        assertEquals( 200, response.getStatus().getCode() );
        assertEquals( "text/css", response.getEntity().getMediaType().toString() );
        assertEquals( "p {color:red;}", response.getEntityAsText() );
    }

    @Test
    public void getPublicResource_moduleNotFound()
        throws Exception
    {
        final Request request = new Request( Method.GET, "/live/path/to/content/_/public/demo/css/main.css" );
        final Response response = executeRequest( request );

        assertEquals( 404, response.getStatus().getCode() );
        assertEquals( "Module [demo] not found", response.getStatus().getDescription() );
    }

    @Test
    public void getPublicResource_fileNotFound()
        throws Exception
    {
        final Request request = new Request( Method.GET, "/live/path/to/content/_/public/demo-1.0.0/css/main.css" );
        final Response response = executeRequest( request );

        assertEquals( 404, response.getStatus().getCode() );
        assertEquals( "File [css/main.css] not found in module [demo-1.0.0]", response.getStatus().getDescription() );
    }

    @Test
    public void methodNotAllowed()
        throws Exception
    {
        final Request request = new Request( Method.POST, "/live/path/to/content/_/public/demo-1.0.0/css/main.css" );
        final Response response = executeRequest( request );
        assertEquals( 405, response.getStatus().getCode() );
    }

    @Test
    public final void illegalMode()
        throws Exception
    {
        final Request request = new Request( Method.GET, "/unknown/path/to/content/_/public/demo-1.0.0/css/main.css" );
        final Response response = executeRequest( request );

        assertEquals( 404, response.getStatus().getCode() );
        assertEquals( "Illegal mode [unknown]. Should be one of [edit,preview,live].", response.getStatus().getDescription() );
    }
}
