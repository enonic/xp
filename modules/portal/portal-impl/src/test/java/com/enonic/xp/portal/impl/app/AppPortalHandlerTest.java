package com.enonic.xp.portal.impl.app;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith( MockitoExtension.class )
class AppPortalHandlerTest {

    private AppPortalHandler appPortalHandler;

    @Mock
    private WebRequest webRequest;

    @Mock
    private WebResponse webResponse;

    @BeforeEach
    void setUp() {
        appPortalHandler = new AppPortalHandler();
    }

    @Test
    void canHandle_withWebAppPath_returnsTrue() {
        when(webRequest.getRawPath()).thenReturn("/webapp/some.app");
        assertTrue(appPortalHandler.canHandle(webRequest));
    }

    @Test
    void canHandle_withoutWebAppPath_returnsFalse() {
        when(webRequest.getRawPath()).thenReturn("/some/other/path");
        assertFalse(appPortalHandler.canHandle(webRequest));
    }

    @Test
    void createPortalRequest_withValidPath_returnsPortalRequest() {
        when(webRequest.getRawPath()).thenReturn("/webapp/some.app");
        PortalRequest portalRequest = appPortalHandler.createPortalRequest(webRequest, webResponse);
        assertEquals("/webapp/some.app", portalRequest.getBaseUri());
        assertEquals("some.app", portalRequest.getApplicationKey().toString());
    }

    @Test
    void createPortalRequest_withoutApplication_throwsException() {
        when(webRequest.getRawPath()).thenReturn("/webapp/");
        assertThrows( WebException.class, () -> appPortalHandler.createPortalRequest( webRequest, webResponse));
    }
}
