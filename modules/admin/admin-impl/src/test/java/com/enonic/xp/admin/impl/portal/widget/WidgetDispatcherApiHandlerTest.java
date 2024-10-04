package com.enonic.xp.admin.impl.portal.widget;

import org.junit.jupiter.api.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WidgetDispatcherApiHandlerTest
{
    @Test
    void testHandle()
    {
        GetListAllowedWidgetsHandler listWidgetsHandler = mock( GetListAllowedWidgetsHandler.class );
        GetWidgetIconHandler getWidgetIconHandler = mock( GetWidgetIconHandler.class );
        WidgetApiHandler widgetApiHandler = mock( WidgetApiHandler.class );

        WidgetDispatcherApiHandler handler = new WidgetDispatcherApiHandler( listWidgetsHandler, getWidgetIconHandler, widgetApiHandler );

        WebRequest webRequest = mock( WebRequest.class );
        when( webRequest.getMethod() ).thenReturn( HttpMethod.GET );
        when( webRequest.getEndpointPath() ).thenReturn( "/_/admin:widget" );
        when( webRequest.getParams() ).thenReturn( HashMultimap.create() );

        WebException ex = assertThrows( WebException.class, () -> handler.handle( webRequest ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );

        // get widget icon
        Multimap<String, String> params = HashMultimap.create();
        params.put( "app", "myapp" );
        params.put( "widget", "mywidget" );
        params.put( "icon", null );

        final WebResponse response = mock( WebResponse.class );
        when( getWidgetIconHandler.handle( webRequest ) ).thenReturn( response );

        when( webRequest.getParams() ).thenReturn( params );
        assertEquals( response, handler.handle( webRequest ) );

        // list widgets
        params = HashMultimap.create();
        params.put( "widgetInterface", "admin.dashboard" );

        when( webRequest.getParams() ).thenReturn( params );
        when( listWidgetsHandler.handle( webRequest ) ).thenReturn( response );
        assertEquals( response, handler.handle( webRequest ) );

        // widget harmonized api
        when( webRequest.getEndpointPath() ).thenReturn( "/_/admin:widget/myapp/mywidget" );
        when( webRequest.getParams() ).thenReturn( null );
        when( widgetApiHandler.handle( webRequest ) ).thenReturn( response );
        assertEquals( response, handler.handle( webRequest ) );
    }
}
