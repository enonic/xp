package com.enonic.xp.admin.impl.portal.extension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.net.HttpHeaders;

import com.enonic.xp.admin.extension.AdminExtensionDescriptor;
import com.enonic.xp.admin.extension.AdminExtensionDescriptorService;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetAdminExtensionIconHandlerTest
{
    private GetAdminExtensionIconHandler instance;

    private AdminExtensionDescriptorService descriptorService;

    private AdminExtensionIconResolver iconResolver;

    @BeforeEach
    void setUp()
    {
        this.descriptorService = mock( AdminExtensionDescriptorService.class );
        this.iconResolver = mock( AdminExtensionIconResolver.class );

        instance = new GetAdminExtensionIconHandler( descriptorService, iconResolver );
    }

    @Test
    void testResolve()
    {
        final WebRequest webRequest = new WebRequest();
        webRequest.getParams().put( "app", "myapp" );
        webRequest.getParams().put( "extension", "myextension" );
        webRequest.getParams().put( "v", "z4PhNX7vuL3xVChQ1m2ABw" );

        final Icon icon = mock( Icon.class );
        when( icon.toByteArray() ).thenReturn( new byte[0] );
        when( icon.getMimeType() ).thenReturn( "image/png" );

        final AdminExtensionDescriptor descriptor = mock( AdminExtensionDescriptor.class );
        when( descriptor.getIcon() ).thenReturn( icon );
        when( descriptor.isAccessAllowed( any() ) ).thenReturn( true );

        when( descriptorService.getByKey( any() ) ).thenReturn( descriptor );
        when( iconResolver.resolve( eq( descriptor ) ) ).thenReturn( icon );

        final WebResponse webResponse = instance.handle( webRequest );
        assertEquals( "public, max-age=31536000, immutable", webResponse.getHeaders().get( HttpHeaders.CACHE_CONTROL ) );
        assertEquals( "nosniff", webResponse.getHeaders().get( HttpHeaders.X_CONTENT_TYPE_OPTIONS ) );
        assertNull( webResponse.getHeaders().get( HttpHeaders.CONTENT_SECURITY_POLICY ) );
    }

    @Test
    void testResolveSvg()
    {
        final WebRequest webRequest = newRequest( "myapp", "myextension" );

        final Icon icon = mock( Icon.class );
        when( icon.toByteArray() ).thenReturn( "<svg/>".getBytes() );
        when( icon.getMimeType() ).thenReturn( "image/svg+xml" );

        final AdminExtensionDescriptor descriptor = mock( AdminExtensionDescriptor.class );
        when( descriptor.isAccessAllowed( any() ) ).thenReturn( true );
        when( descriptorService.getByKey( any() ) ).thenReturn( descriptor );
        when( iconResolver.resolve( eq( descriptor ) ) ).thenReturn( icon );

        final WebResponse webResponse = instance.handle( webRequest );
        assertEquals( HttpStatus.OK, webResponse.getStatus() );
        assertEquals( "nosniff", webResponse.getHeaders().get( HttpHeaders.X_CONTENT_TYPE_OPTIONS ) );
        assertEquals( "sandbox; default-src 'none'; base-uri 'none'; form-action 'none'; style-src 'self' 'unsafe-inline'",
                      webResponse.getHeaders().get( HttpHeaders.CONTENT_SECURITY_POLICY ) );
    }

    @Test
    void testMissingParameters()
    {
        final WebRequest missingApp = new WebRequest();
        missingApp.getParams().put( "extension", "myextension" );
        final WebException noApp = assertThrows( WebException.class, () -> instance.handle( missingApp ) );
        assertEquals( HttpStatus.BAD_REQUEST, noApp.getStatus() );
        assertEquals( "Missing parameter: app", noApp.getMessage() );

        final WebRequest missingExtension = new WebRequest();
        missingExtension.getParams().put( "app", "myapp" );
        final WebException noExtension = assertThrows( WebException.class, () -> instance.handle( missingExtension ) );
        assertEquals( HttpStatus.BAD_REQUEST, noExtension.getStatus() );
        assertEquals( "Missing parameter: extension", noExtension.getMessage() );
    }

    @Test
    void testInvalidExtensionName()
    {
        final WebException ex = assertThrows( WebException.class, () -> instance.handle( newRequest( "myapp", "my extension" ) ) );
        assertEquals( HttpStatus.BAD_REQUEST, ex.getStatus() );
        assertEquals( "Invalid extension name: my extension", ex.getMessage() );
    }

    @Test
    void testExtensionNotFound()
    {
        final WebException ex = assertThrows( WebException.class, () -> instance.handle( newRequest( "myapp", "myextension" ) ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
        assertEquals( "Extension [myapp:myextension] not found", ex.getMessage() );
    }

    @Test
    void testAccessDenied()
    {
        final AdminExtensionDescriptor descriptor = mock( AdminExtensionDescriptor.class );
        when( descriptor.isAccessAllowed( any() ) ).thenReturn( false );
        when( descriptorService.getByKey( any() ) ).thenReturn( descriptor );

        final WebException ex = assertThrows( WebException.class, () -> instance.handle( newRequest( "myapp", "myextension" ) ) );
        assertEquals( HttpStatus.UNAUTHORIZED, ex.getStatus() );
        assertEquals( "You don't have permission to access [myapp:myextension]", ex.getMessage() );
    }

    private static WebRequest newRequest( final String app, final String extension )
    {
        final WebRequest webRequest = new WebRequest();
        webRequest.getParams().put( "app", app );
        webRequest.getParams().put( "extension", extension );
        return webRequest;
    }

    @Test
    void testResolveInvalidArgument()
    {
        final WebRequest webRequest = new WebRequest();
        webRequest.getParams().put( "app", "<>" );
        webRequest.getParams().put( "extension", "myextension" );
        webRequest.getParams().put( "v", "d41d8cd98f00b204e9800998ecf8427e" );

        final WebException ex = assertThrows( WebException.class, () -> instance.handle( webRequest ) );
        assertEquals( "Invalid application key: <>", ex.getMessage() );
    }
}
