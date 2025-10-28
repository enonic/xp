package com.enonic.xp.admin.impl.portal.extension;

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

class AdminExtensionDispatcherApiHandlerTest
{
    @Test
    void testHandle()
    {
        GetListAllowedAdminExtensionsHandler listExtensionsHandler = mock( GetListAllowedAdminExtensionsHandler.class );
        GetAdminExtensionIconHandler getExtensionIconHandler = mock( GetAdminExtensionIconHandler.class );
        AdminExtensionApiHandler extensionApiHandler = mock( AdminExtensionApiHandler.class );

        AdminExtensionDispatcherApiHandler
            handler = new AdminExtensionDispatcherApiHandler( listExtensionsHandler, getExtensionIconHandler, extensionApiHandler );

        WebRequest webRequest = mock( WebRequest.class );
        when( webRequest.getMethod() ).thenReturn( HttpMethod.GET );
        when( webRequest.getEndpointPath() ).thenReturn( "/_/admin:extension" );
        when( webRequest.getParams() ).thenReturn( HashMultimap.create() );

        WebException ex = assertThrows( WebException.class, () -> handler.handle( webRequest ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );

        // get extension icon
        Multimap<String, String> params = HashMultimap.create();
        params.put( "app", "myapp" );
        params.put( "extension", "myextension" );
        params.put( "icon", null );

        final WebResponse response = mock( WebResponse.class );
        when( getExtensionIconHandler.handle( webRequest ) ).thenReturn( response );

        when( webRequest.getParams() ).thenReturn( params );
        assertEquals( response, handler.handle( webRequest ) );

        // list extensions
        params = HashMultimap.create();
        params.put( "interface", "admin.dashboard" );

        when( webRequest.getParams() ).thenReturn( params );
        when( listExtensionsHandler.handle( webRequest ) ).thenReturn( response );
        assertEquals( response, handler.handle( webRequest ) );

        // extension harmonized api
        when( webRequest.getEndpointPath() ).thenReturn( "/_/admin:extension/myapp/myextension" );
        when( webRequest.getParams() ).thenReturn( null );
        when( extensionApiHandler.handle( webRequest ) ).thenReturn( response );
        assertEquals( response, handler.handle( webRequest ) );
    }
}
