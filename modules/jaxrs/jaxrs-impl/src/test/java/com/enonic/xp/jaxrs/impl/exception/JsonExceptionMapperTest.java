package com.enonic.xp.jaxrs.impl.exception;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import com.enonic.xp.server.RunMode;
import com.enonic.xp.server.RunModeSupport;

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

    @AfterEach
    void resetRunMode()
    {
        RunModeSupport.set( RunMode.PROD );
    }

    @Test
    void testCreateErrorJson()
    {
        final IOException cause = new IOException();
        final ObjectNode json = JsonExceptionMapper.createErrorJson( cause, 500 );
        assertNotNull( json );
    }

    @Test
    void testCreateErrorJson_serverErrorHidesDetailsInProd()
    {
        RunModeSupport.set( RunMode.PROD );

        final ObjectNode json = JsonExceptionMapper.createErrorJson( new IOException( "/opt/xp/home/repo/blob is not writable" ), 500 );
        assertEquals( "Internal Server Error", json.get( "message" ).asText() );
    }

    @Test
    void testCreateErrorJson_serverErrorKeepsDetailsInDev()
    {
        RunModeSupport.set( RunMode.DEV );

        final ObjectNode json = JsonExceptionMapper.createErrorJson( new IOException( "details" ), 500 );
        assertEquals( "details", json.get( "message" ).asText() );
    }

    @Test
    void testCreateErrorJson_clientErrorKeepsMessage()
    {
        RunModeSupport.set( RunMode.PROD );

        final ObjectNode json = JsonExceptionMapper.createErrorJson( new IllegalArgumentException( "bad input" ), 400 );
        assertEquals( "bad input", json.get( "message" ).asText() );
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
