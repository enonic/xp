package com.enonic.wem.portal2.http;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HttpStatusTest
{
    private Set<Integer> codes;

    @Before
    public void createCodes()
    {
        this.codes = new HashSet<>();

        this.codes.add( 100 );

        this.codes.add( 200 );
        this.codes.add( 201 );
        this.codes.add( 202 );
        this.codes.add( 204 );

        this.codes.add( 301 );
        this.codes.add( 302 );
        this.codes.add( 303 );
        this.codes.add( 304 );
        this.codes.add( 305 );
        this.codes.add( 307 );

        this.codes.add( 400 );
        this.codes.add( 401 );
        this.codes.add( 402 );
        this.codes.add( 403 );
        this.codes.add( 404 );
        this.codes.add( 405 );
        this.codes.add( 406 );
        this.codes.add( 407 );
        this.codes.add( 408 );
        this.codes.add( 409 );
        this.codes.add( 410 );
        this.codes.add( 411 );
        this.codes.add( 412 );
        this.codes.add( 415 );
        this.codes.add( 429 );

        this.codes.add( 500 );
        this.codes.add( 501 );
        this.codes.add( 502 );
        this.codes.add( 503 );
        this.codes.add( 504 );
    }

    @Test
    public void isInformational()
    {
        for ( final HttpStatus status : HttpStatus.values() )
        {
            if ( ( status.getCode() / 100 ) == 1 )
            {
                assertTrue( status.isInformational() );
            }
        }
    }

    @Test
    public void isSuccessful()
    {
        for ( final HttpStatus status : HttpStatus.values() )
        {
            if ( ( status.getCode() / 100 ) == 2 )
            {
                assertTrue( status.isSuccessful() );
            }
        }
    }

    @Test
    public void isRedirection()
    {
        for ( final HttpStatus status : HttpStatus.values() )
        {
            if ( ( status.getCode() / 100 ) == 3 )
            {
                assertTrue( status.isRedirection() );
            }
        }
    }

    @Test
    public void isClientError()
    {
        for ( final HttpStatus status : HttpStatus.values() )
        {
            if ( ( status.getCode() / 100 ) == 4 )
            {
                assertTrue( status.isClientError() );
            }
        }
    }

    @Test
    public void isServerError()
    {
        for ( final HttpStatus status : HttpStatus.values() )
        {
            if ( ( status.getCode() / 100 ) == 5 )
            {
                assertTrue( status.isServerError() );
            }
        }
    }

    @Test
    public void testToString()
    {
        for ( final HttpStatus status : HttpStatus.values() )
        {
            assertEquals( String.valueOf( status.getCode() ), status.toString() );
        }
    }

    @Test
    public void fromCodeToEnum()
    {
        for ( final int code : this.codes )
        {
            final HttpStatus status = HttpStatus.valueOf( code );
            assertEquals( code, status.getCode() );
            assertNotNull( status.getReasonPhrase() );
        }
    }

    @Test
    public void fromEnumToMap()
    {
        for ( final HttpStatus status : HttpStatus.values() )
        {
            final int code = status.getCode();
            assertTrue( "Code [" + code + "] not in set", this.codes.contains( code ) );
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void unknownValueOfCode()
    {
        HttpStatus.valueOf( 999 );
    }
}
