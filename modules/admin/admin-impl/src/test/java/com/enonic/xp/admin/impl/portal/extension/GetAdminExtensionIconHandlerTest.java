package com.enonic.xp.admin.impl.portal.extension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.net.HttpHeaders;

import com.enonic.xp.admin.extension.AdminExtensionDescriptor;
import com.enonic.xp.admin.extension.AdminExtensionDescriptorService;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

        when( descriptorService.getByKey( any() ) ).thenReturn( descriptor );
        when( iconResolver.resolve( eq( descriptor ) ) ).thenReturn( icon );

        final WebResponse webResponse = instance.handle( webRequest );
        assertEquals( "public, max-age=31536000, immutable", webResponse.getHeaders().get( HttpHeaders.CACHE_CONTROL ) );
    }

    @Test
    void testResolveInvalidArgument()
    {
        final WebRequest webRequest = new WebRequest();
        webRequest.getParams().put( "app", "<>" );
        webRequest.getParams().put( "extension", "myextension" );
        webRequest.getParams().put( "v", "d41d8cd98f00b204e9800998ecf8427e" );

        final IllegalArgumentException ex = assertThrows( IllegalArgumentException.class, () -> instance.handle( webRequest ) );
        assertEquals( "Invalid application key: <>", ex.getMessage() );
    }
}
