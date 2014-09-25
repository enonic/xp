package com.enonic.wem.portal.internal.exception;

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.Representation;

import com.google.common.base.Charsets;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceProblemException;
import com.enonic.wem.api.resource.ResourceUrlTestHelper;
import com.enonic.wem.core.entity.dao.NodeNotFoundException;

import static org.junit.Assert.*;

public class PortalStatusServiceTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private PortalStatusService service;

    @Before
    public void setup()
    {
        this.service = new PortalStatusService();
    }

    @Test
    public void testMapping_RuntimeException()
    {
        final RuntimeException exception = new RuntimeException( "Message" );
        final Status status = getStatus( exception );

        assertNotNull( status );
        assertEquals( 500, status.getCode() );
        assertSame( exception, status.getThrowable() );
    }

    @Test
    public void testMapping_NodeNotFoundException()
    {
        final NodeNotFoundException exception = new NodeNotFoundException( "Message" );
        final Status status = getStatus( exception );

        assertNotNull( status );
        assertEquals( 404, status.getCode() );
        assertSame( exception, status.getThrowable() );
    }

    @Test
    public void testRepresentation_serverError()
    {
        final RuntimeException exception = new RuntimeException( "Message" );
        final Status status = new Status( 500, exception );
        final Representation representation = getRepresentation( status );

        assertNotNull( representation );
        assertEquals( MediaType.TEXT_HTML, representation.getMediaType() );
    }

    @Test
    public void testRepresentation_otherError()
    {
        final RuntimeException exception = new RuntimeException( "Message" );
        final Status status = new Status( 404, exception );
        final Representation representation = getRepresentation( status );

        assertNotNull( representation );
        assertEquals( MediaType.TEXT_HTML, representation.getMediaType() );
    }

    @Test
    public void testRepresentation_SourceException()
        throws Exception
    {
        ResourceUrlTestHelper.mockModuleScheme().modulesDir( this.temporaryFolder.getRoot() );

        final String source = "var i = 3;";
        final File file = this.temporaryFolder.newFile( "source.js" );

        Files.write( file.toPath(), Collections.singleton( source ), Charsets.UTF_8 );

        final ResourceProblemException exception = ResourceProblemException.newBuilder().
            lineNumber( 1 ).
            resource( ResourceKey.from( "mymodule:source.js" ) ).
            build();
        final Status status = new Status( 500, exception );
        final Representation representation = getRepresentation( status );

        assertNotNull( representation );
        assertEquals( MediaType.TEXT_HTML, representation.getMediaType() );
    }

    private Status getStatus( final Throwable cause )
    {
        final Request request = new Request( Method.GET, "/dummy" );
        final Response response = new Response( request );
        return this.service.getStatus( cause, request, response );
    }

    private Representation getRepresentation( final Status status )
    {
        final Request request = new Request( Method.GET, "/dummy" );
        final Response response = new Response( request );
        return this.service.getRepresentation( status, request, response );
    }
}
