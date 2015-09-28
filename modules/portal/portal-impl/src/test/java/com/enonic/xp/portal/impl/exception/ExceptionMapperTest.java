package com.enonic.xp.portal.impl.exception;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.exception.NotFoundException;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.PortalException;
import com.enonic.xp.web.HttpStatus;

import static org.junit.Assert.*;

public class ExceptionMapperTest
{
    private ExceptionMapper mapper;

    @Before
    public void setup()
    {
        this.mapper = new ExceptionMapper();
    }

    @Test
    public void map_portalException()
    {
        final PortalException result = this.mapper.map( new PortalException( HttpStatus.NOT_FOUND, "Custom message" ) );
        assertNotNull( result );
        assertEquals( HttpStatus.NOT_FOUND, result.getStatus() );
        assertEquals( "Custom message", result.getMessage() );
    }

    @Test
    public void map_notFoundException()
    {
        final PortalException result = this.mapper.map( new NotFoundException( "Custom message" )
        {
        } );
        assertNotNull( result );
        assertEquals( HttpStatus.NOT_FOUND, result.getStatus() );
        assertEquals( "Custom message", result.getMessage() );
    }

    @Test
    public void map_illegalArgumentException()
    {
        final PortalException result = this.mapper.map( new IllegalArgumentException( "Custom message" ) );
        assertNotNull( result );
        assertEquals( HttpStatus.BAD_REQUEST, result.getStatus() );
        assertEquals( "Custom message", result.getMessage() );
    }

    @Test
    public void map_otherException()
    {
        final PortalException result = this.mapper.map( new RuntimeException( "Custom message" ) );
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
        catch ( final PortalException e )
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
