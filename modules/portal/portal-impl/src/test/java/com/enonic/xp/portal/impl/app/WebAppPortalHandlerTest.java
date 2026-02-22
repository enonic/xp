package com.enonic.xp.portal.impl.app;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class WebAppPortalHandlerTest
{
    private WebAppPortalHandler webappPortalHandler;

    @BeforeEach
    void setUp()
    {
        webappPortalHandler = new WebAppPortalHandler( mock(), mock() );
    }

    @Test
    void canHandle_withWebAppPath_returnsTrue()
    {
        final WebRequest webRequest = new WebRequest();
        webRequest.setRawPath( "/webapp/some.app" );

        assertTrue( webappPortalHandler.canHandle( webRequest ) );
    }

    @Test
    void canHandle_withoutWebAppPath_returnsFalse()
    {
        final WebRequest webRequest = new WebRequest();
        webRequest.setRawPath( "/some/other/path" );

        assertFalse( webappPortalHandler.canHandle( webRequest ) );
    }

    @Test
    void createPortalRequest_withValidPath_returnsPortalRequest()
    {
        final WebRequest webRequest = new WebRequest();
        webRequest.setRawPath( "/webapp/some.app" );

        final WebResponse webResponse = WebResponse.create().build();

        PortalRequest portalRequest = webappPortalHandler.createPortalRequest( webRequest, webResponse );
        assertEquals( "/webapp/some.app", portalRequest.getBaseUri() );
        assertEquals( "some.app", portalRequest.getApplicationKey().toString() );
    }

    @Test
    void createPortalRequest_withoutApplication_throwsException()
    {
        final WebRequest webRequest = new WebRequest();
        webRequest.setRawPath( "/webapp/" );

        final WebResponse webResponse = WebResponse.create().build();

        assertThrows( WebException.class, () -> webappPortalHandler.createPortalRequest( webRequest, webResponse ) );
    }
}
