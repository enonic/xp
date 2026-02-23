package com.enonic.xp.jaxrs.impl.exception;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

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
    void testToResponse_general()
    {
        final Response res = this.mapper.toResponse( new IOException() );
        assertEquals( 500, res.getStatus() );
    }
}
