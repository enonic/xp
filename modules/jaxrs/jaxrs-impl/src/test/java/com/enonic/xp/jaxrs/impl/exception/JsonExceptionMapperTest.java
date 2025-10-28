package com.enonic.xp.jaxrs.impl.exception;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import com.enonic.xp.content.PushContentException;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JsonExceptionMapperTest
{
    private JsonExceptionMapper mapper;

    @BeforeEach
    void setup()
    {
        this.mapper = new JsonExceptionMapper();
    }

    @Test
    void testShouldLogAsError()
    {
        assertEquals( true, JsonExceptionMapper.shouldLogAsError( 500 ) );
        assertEquals( true, JsonExceptionMapper.shouldLogAsError( 400 ) );
        assertEquals( false, JsonExceptionMapper.shouldLogAsError( 401 ) );
    }

    @Test
    void testLogIfNeeded()
    {
        final IOException cause = new IOException();

        // Should log
        JsonExceptionMapper.logErrorIfNeeded( cause, 500 );

        // Should not log
        JsonExceptionMapper.logErrorIfNeeded( cause, 401 );
    }

    @Test
    void testCreateErrorJson()
    {
        final IOException cause = new IOException();
        final ObjectNode json = JsonExceptionMapper.createErrorJson( cause, 500 );
        assertNotNull( json );
    }

    @Test
    void testToResponse_WebApplicationException()
    {
        final Response res = this.mapper.toResponse( new WebApplicationException( 401 ) );
        assertEquals( 401, res.getStatus() );
    }

    @Test
    void testToResponse_NotFoundException()
    {
        final Response res = this.mapper.toResponse( new ResourceNotFoundException( ResourceKey.from( "app:/a/b/c" ) ) );
        assertEquals( 404, res.getStatus() );
    }

    @Test
    void testToResponse_IllegalArgumentException()
    {
        final Response res = this.mapper.toResponse( new IllegalArgumentException() );
        assertEquals( 400, res.getStatus() );
    }

    @Test
    void testToResponse_PushContentException()
    {
        final Response res = this.mapper.toResponse( new PushContentException( "test" ) );
        assertEquals( 400, res.getStatus() );
    }

    @Test
    void testToResponse_general()
    {
        final Response res = this.mapper.toResponse( new IOException() );
        assertEquals( 500, res.getStatus() );
    }
}
