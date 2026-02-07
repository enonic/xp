package com.enonic.xp.admin.impl.portal.widget;

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

class WidgetDispatcherApiHandlerTest
{
    @Test
    void testHandle()
    {
        GetListAllowedWidgetsHandler listWidgetsHandler = mock( GetListAllowedWidgetsHandler.class );
        GetWidgetIconHandler getWidgetIconHandler = mock( GetWidgetIconHandler.class );
        WidgetApiHandler widgetApiHandler = mock( WidgetApiHandler.class );

        WidgetDispatcherApiHandler handler = new WidgetDispatcherApiHandler( listWidgetsHandler, getWidgetIconHandler, widgetApiHandler );

        final WebRequest webRequest1 = new WebRequest();
        webRequest1.setMethod( HttpMethod.GET );
        webRequest1.setRawPath( "/path/_/admin:widget" );

        WebException ex = assertThrows( WebException.class, () -> handler.handle( webRequest1 ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );

        // get widget icon
        final WebRequest webRequest2 = new WebRequest();
        webRequest2.setMethod( HttpMethod.GET );
        webRequest2.setRawPath( "/path/_/admin:widget" );
        webRequest2.getParams().put( "app", "myapp" );
        webRequest2.getParams().put( "widget", "mywidget" );
        webRequest2.getParams().put( "icon", null );

        final WebResponse response = mock( WebResponse.class );
        when( getWidgetIconHandler.handle( webRequest2 ) ).thenReturn( response );

        assertEquals( response, handler.handle( webRequest2 ) );

        // list widgets
        final WebRequest webRequest3 = new WebRequest();
        webRequest3.setMethod( HttpMethod.GET );
        webRequest3.setRawPath( "/path/_/admin:widget" );
        webRequest3.getParams().put( "widgetInterface", "admin.dashboard" );

        when( listWidgetsHandler.handle( webRequest3 ) ).thenReturn( response );
        assertEquals( response, handler.handle( webRequest3 ) );

        // widget harmonized api
        final WebRequest webRequest4 = new WebRequest();
        webRequest4.setMethod( HttpMethod.GET );
        webRequest4.setRawPath( "/path/_/admin:widget/myapp/mywidget" );

        when( widgetApiHandler.handle( webRequest4 ) ).thenReturn( response );
        assertEquals( response, handler.handle( webRequest4 ) );
    }
}
