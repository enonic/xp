package com.enonic.xp.portal.impl.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.exception.NotFoundException;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.impl.exception.ExceptionMapperImpl;

import static org.junit.jupiter.api.Assertions.*;

public class ExceptionMapperTest
{
    private ExceptionMapperImpl mapper;

    @BeforeEach
    public void setup()
    {
        this.mapper = new ExceptionMapperImpl();
    }

    @Test
    public void map_portalException()
    {
        final WebException result = this.mapper.map( new WebException( HttpStatus.NOT_FOUND, "Custom message" ) );
        assertNotNull( result );
        assertEquals( HttpStatus.NOT_FOUND, result.getStatus() );
        assertEquals( "Custom message", result.getMessage() );
    }

    @Test
    public void map_notFoundException()
    {
        final WebException result = this.mapper.map( new NotFoundException( "Custom message" )
        {
        } );
        assertNotNull( result );
        assertEquals( HttpStatus.NOT_FOUND, result.getStatus() );
        assertEquals( "Custom message", result.getMessage() );
    }

    @Test
    public void map_illegalArgumentException()
    {
        final WebException result = this.mapper.map( new IllegalArgumentException( "Custom message" ) );
        assertNotNull( result );
        assertEquals( HttpStatus.BAD_REQUEST, result.getStatus() );
        assertEquals( "Custom message", result.getMessage() );
    }

    @Test
    public void map_otherException()
    {
        final WebException result = this.mapper.map( new RuntimeException( "Custom message" ) );
        assertNotNull( result );
        assertEquals( HttpStatus.INTERNAL_SERVER_ERROR, result.getStatus() );
        assertEquals( "Custom message", result.getMessage() );
    }

    @Test
    public void throwIfNeeded()
    {
        assertThrowIfNeeded( HttpStatus.BAD_REQUEST );
        assertThrowIfNeeded( HttpStatus.INTERNAL_SERVER_ERROR );
    }

    private void assertThrowIfNeeded( final HttpStatus status )
    {
        final PortalResponse response = PortalResponse.create().
            status( status ).
            build();

        try
        {
            this.mapper.throwIfNeeded( response );
            fail( "Should throw exception" );
        }
        catch ( final WebException e )
        {
            assertEquals( status, e.getStatus() );
        }
    }

    @Test
    public void throwIfNeeded_notNeeded()
    {
        final PortalResponse response = PortalResponse.create().
            status( HttpStatus.OK ).
            build();

        this.mapper.throwIfNeeded( response );
    }
}
