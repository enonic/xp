package com.enonic.wem.portal.internal.exception;

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Charsets;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceProblemException;
import com.enonic.wem.api.resource.ResourceUrlTestHelper;
import com.enonic.wem.core.entity.dao.NodeNotFoundException;

import static org.junit.Assert.*;

public class PortalExceptionMapperTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private PortalExceptionMapper mapper;

    @Before
    public void setup()
    {
        this.mapper = new PortalExceptionMapper();
    }

    @Test
    public void testMapping_RuntimeException()
    {
        final RuntimeException exception = new RuntimeException( "Message" );
        final Response response = this.mapper.toResponse( exception );

        assertNotNull( response );
        assertEquals( 500, response.getStatus() );
        assertEquals( MediaType.TEXT_HTML_TYPE, response.getMediaType() );
    }

    @Test
    public void testMapping_NodeNotFoundException()
    {
        final NodeNotFoundException exception = new NodeNotFoundException( "Message" );
        final Response response = this.mapper.toResponse( exception );

        assertNotNull( response );
        assertEquals( 404, response.getStatus() );
        assertEquals( MediaType.TEXT_HTML_TYPE, response.getMediaType() );
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

        final Response response = this.mapper.toResponse( exception );

        assertNotNull( response );
        assertEquals( MediaType.TEXT_HTML_TYPE, response.getMediaType() );
    }
}
