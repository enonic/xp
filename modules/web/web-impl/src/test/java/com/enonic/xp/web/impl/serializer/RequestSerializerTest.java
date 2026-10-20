package com.enonic.xp.web.impl.serializer;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RequestSerializerTest
{
    @Test
    void virtualHostFromTheMatchedMapping()
    {
        final HttpServletRequest request = mock( HttpServletRequest.class );
        when( request.getMethod() ).thenReturn( "GET" );
        when( request.getScheme() ).thenReturn( "http" );
        when( request.getServerName() ).thenReturn( "localhost" );
        when( request.getPathInfo() ).thenReturn( "/" );
        when( request.getRequestURI() ).thenReturn( "/" );
        when( request.getLocales() ).thenReturn( Collections.emptyEnumeration() );
        when( request.getHeaderNames() ).thenReturn( Collections.emptyEnumeration() );
        when( request.getParameterMap() ).thenReturn( Map.of() );
        final WebRequest none = new WebRequest();
        new RequestSerializer( none ).serialize( request );
        assertNull( none.getVirtualHost() );

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getSource() ).thenReturn( "/" );
        when( virtualHost.getTarget() ).thenReturn( "/" );
        VirtualHostHelper.setVirtualHost( request, virtualHost );
        when( request.getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );
        final WebRequest matched = new WebRequest();
        new RequestSerializer( matched ).serialize( request );
        assertSame( virtualHost, matched.getVirtualHost() );
    }

    @Test
    void unknownMethod()
    {
        final HttpServletRequest request = mock( HttpServletRequest.class );
        when( request.getMethod() ).thenReturn( "BREW" );

        final WebException e = assertThrows( WebException.class, () -> new RequestSerializer( new WebRequest() ).serialize( request ) );
        assertEquals( HttpStatus.METHOD_NOT_ALLOWED, e.getStatus() );
    }
}
