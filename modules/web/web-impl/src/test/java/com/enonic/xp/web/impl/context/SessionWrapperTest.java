package com.enonic.xp.web.impl.context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.session.Session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SessionWrapperTest
{
    private HttpServletRequest request;

    private HttpSession httpSession;

    private Session session;

    @BeforeEach
    void setUp()
    {
        this.request = mock( HttpServletRequest.class );
        this.httpSession = mock( HttpSession.class );
        when( this.request.getSession( false ) ).thenReturn( this.httpSession );
        when( this.request.getSession( true ) ).thenReturn( this.httpSession );
        when( this.httpSession.getId() ).thenReturn( "test-session-id" );
        this.session = new SessionWrapper( this.request );
    }

    @Test
    void testChangeSessionId()
    {
        final String newSessionId = "new-session-id";
        when( this.request.changeSessionId() ).thenReturn( newSessionId );

        final String result = this.session.changeSessionId();

        assertEquals( newSessionId, result );
        verify( this.request ).changeSessionId();
    }

    @Test
    void testSetMaxInactiveInterval()
    {
        final int seconds = 3600;

        this.session.setMaxInactiveInterval( seconds );

        verify( this.httpSession ).setMaxInactiveInterval( seconds );
    }

    @Test
    void testSetMaxInactiveInterval_noSession()
    {
        when( this.request.getSession( false ) ).thenReturn( null );
        final Session sessionWithoutHttpSession = new SessionWrapper( this.request );

        // Should not throw exception when session is null
        sessionWithoutHttpSession.setMaxInactiveInterval( 3600 );
    }
}
