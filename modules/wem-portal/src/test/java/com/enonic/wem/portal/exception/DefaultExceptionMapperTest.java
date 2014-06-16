package com.enonic.wem.portal.exception;

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Charsets;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.portal.script.SourceException;

import static org.junit.Assert.*;

public class DefaultExceptionMapperTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private DefaultExceptionMapper mapper;

    @Before
    public void setup()
    {
        this.mapper = new DefaultExceptionMapper();
    }

    @Test
    public void test_RuntimeException()
    {
        final RuntimeException exception = new RuntimeException( "Message" );
        final Response response = this.mapper.toResponse( exception );
        assertEquals( 500, response.getStatus() );
    }

    @Test
    public void test_ModuleNotFoundException()
    {
        final ModuleNotFoundException exception = new ModuleNotFoundException( ModuleKey.from( "mymodule-1.0.0" ) );
        final Response response = this.mapper.toResponse( exception );
        assertEquals( 404, response.getStatus() );
    }

    @Test
    public void test_WebApplicationException()
    {
        final WebApplicationException exception = new WebApplicationException( Response.Status.BAD_REQUEST );
        final Response response = this.mapper.toResponse( exception );
        assertEquals( 400, response.getStatus() );
    }

    @Test
    public void test_SourceException()
        throws Exception
    {
        final String source = "var i = 3;";
        final File file = this.temporaryFolder.newFile( "source.js" );
        Files.write( file.toPath(), Collections.singleton( source ), Charsets.UTF_8 );

        final SourceException exception = SourceException.newBuilder().
            lineNumber( 1 ).
            path( file.toPath() ).
            resource( ModuleResourceKey.from( "mymodule-1.0.0:source.js" ) ).
            build();

        final Response response = this.mapper.toResponse( exception );
        assertEquals( 500, response.getStatus() );
    }
}
