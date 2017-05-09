package com.enonic.xp.jaxrs.impl.exception;

import java.io.IOException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.content.PushContentException;
import com.enonic.xp.market.MarketException;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceNotFoundException;

import static org.junit.Assert.*;

public class JsonExceptionMapperTest
{
    private JsonExceptionMapper mapper;

    @Before
    public void setup()
    {
        this.mapper = new JsonExceptionMapper();
    }

    @Test
    public void testShouldLogAsError()
    {
        assertEquals( true, JsonExceptionMapper.shouldLogAsError( 500 ) );
        assertEquals( true, JsonExceptionMapper.shouldLogAsError( 400 ) );
        assertEquals( false, JsonExceptionMapper.shouldLogAsError( 401 ) );
    }

    @Test
    public void testLogIfNeeded()
    {
        final IOException cause = new IOException();

        // Should log
        JsonExceptionMapper.logErrorIfNeeded( cause, 500 );

        // Should not log
        JsonExceptionMapper.logErrorIfNeeded( cause, 401 );
    }

    @Test
    public void testCreateErrorJson()
    {
        final IOException cause = new IOException();
        final ObjectNode json = JsonExceptionMapper.createErrorJson( cause, 500 );
        assertNotNull( json );
    }

    @Test
    public void testToResponse_WebApplicationException()
    {
        final Response res = this.mapper.toResponse( new WebApplicationException( 401 ) );
        assertEquals( 401, res.getStatus() );
    }

    @Test
    public void testToResponse_NotFoundException()
    {
        final Response res = this.mapper.toResponse( new ResourceNotFoundException( ResourceKey.from( "app:/a/b/c" ) ) );
        assertEquals( 404, res.getStatus() );
    }

    @Test
    public void testToResponse_IllegalArgumentException()
    {
        final Response res = this.mapper.toResponse( new IllegalArgumentException() );
        assertEquals( 400, res.getStatus() );
    }

    @Test
    public void testToResponse_PushContentException()
    {
        final Response res = this.mapper.toResponse( new PushContentException( "test" ) );
        assertEquals( 400, res.getStatus() );
    }

    @Test
    public void testToResponse_MarketException()
    {
        final Response res = this.mapper.toResponse( new MarketException( "test", 404 ) );
        assertEquals( 404, res.getStatus() );
    }

    @Test
    public void testToResponse_general()
    {
        final Response res = this.mapper.toResponse( new IOException() );
        assertEquals( 500, res.getStatus() );
    }
}
