package com.enonic.xp.web.impl.serializer;

import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RequestSerializerTest
{
    @Test
    void unknownMethod()
    {
        final HttpServletRequest request = mock( HttpServletRequest.class );
        when( request.getMethod() ).thenReturn( "BREW" );

        final WebException e = assertThrows( WebException.class, () -> new RequestSerializer( new WebRequest() ).serialize( request ) );
        assertEquals( HttpStatus.METHOD_NOT_ALLOWED, e.getStatus() );
    }
}
