package com.enonic.xp.admin.impl.portal.extension;

import org.junit.jupiter.api.Test;

import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdminExtensionDispatcherApiHandlerTest
{
    @Test
    void testHandle()
    {
        GetListAllowedAdminExtensionsHandler listExtensionsHandler = mock( GetListAllowedAdminExtensionsHandler.class );
        GetAdminExtensionIconHandler getExtensionIconHandler = mock( GetAdminExtensionIconHandler.class );
        AdminExtensionApiHandler extensionApiHandler = mock( AdminExtensionApiHandler.class );

        AdminExtensionDispatcherApiHandler handler =
            new AdminExtensionDispatcherApiHandler( listExtensionsHandler, getExtensionIconHandler, extensionApiHandler );

        final WebRequest webRequest1 = new WebRequest();
        webRequest1.setMethod( HttpMethod.GET );
        webRequest1.setRawPath( "/path/_/admin:extension" );

        WebException ex = assertThrows( WebException.class, () -> handler.handle( webRequest1 ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );

        // get widget icon
        final WebRequest webRequest2 = new WebRequest();
        webRequest2.setMethod( HttpMethod.GET );
        webRequest2.setRawPath( "/path/_/admin:extension" );
        webRequest2.getParams().put( "app", "myapp" );
        webRequest2.getParams().put( "widget", "mywidget" );
        webRequest2.getParams().put( "icon", null );

        final WebResponse response = mock( WebResponse.class );
        when( getExtensionIconHandler.handle( webRequest2 ) ).thenReturn( response );

        assertEquals( response, handler.handle( webRequest2 ) );

        // list widgets
        final WebRequest webRequest3 = new WebRequest();
        webRequest3.setMethod( HttpMethod.GET );
        webRequest3.setRawPath( "/path/_/admin:extension" );
        webRequest3.getParams().put( "widgetInterface", "admin.dashboard" );

        // widget harmonized api
        final WebRequest webRequest4 = new WebRequest();
        webRequest4.setMethod( HttpMethod.GET );
        webRequest4.setRawPath( "/path/_/admin:extension/myapp:mywidget" );

        when( extensionApiHandler.handle( webRequest4 ) ).thenReturn( response );
        assertEquals( response, handler.handle( webRequest4 ) );
    }
}
