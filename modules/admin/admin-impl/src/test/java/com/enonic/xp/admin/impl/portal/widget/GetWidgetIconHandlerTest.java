package com.enonic.xp.admin.impl.portal.widget;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.net.HttpHeaders;

import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.admin.widget.WidgetDescriptorService;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetWidgetIconHandlerTest
{
    private GetWidgetIconHandler instance;

    private WidgetDescriptorService widgetDescriptorService;

    private WidgetIconResolver widgetIconResolver;

    @BeforeEach
    void setUp()
    {
        this.widgetDescriptorService = mock( WidgetDescriptorService.class );
        this.widgetIconResolver = mock( WidgetIconResolver.class );

        instance = new GetWidgetIconHandler( widgetDescriptorService, widgetIconResolver );
    }

    @Test
    void testResolve()
    {
        final Multimap<String, String> params = HashMultimap.create();
        params.put( "app", "myapp" );
        params.put( "widget", "mywidget" );
        params.put( "v", "d41d8cd98f00b204e9800998ecf8427e" );

        final WebRequest webRequest = mock( WebRequest.class );
        when( webRequest.getParams() ).thenReturn( params );

        final Icon icon = mock( Icon.class );
        when( icon.toByteArray() ).thenReturn( new byte[0] );
        when( icon.getMimeType() ).thenReturn( "image/png" );

        final WidgetDescriptor widgetDescriptor = mock( WidgetDescriptor.class );
        when( widgetDescriptor.getIcon() ).thenReturn( icon );

        when( widgetDescriptorService.getByKey( any() ) ).thenReturn( widgetDescriptor );
        when( widgetIconResolver.resolve( eq( widgetDescriptor ) ) ).thenReturn( icon );

        final WebResponse webResponse = instance.handle( webRequest );
        assertEquals( "public, max-age=31536000, immutable", webResponse.getHeaders().get( HttpHeaders.CACHE_CONTROL ) );
    }

    @Test
    void testResolveInvalidArgument()
    {
        final Multimap<String, String> params = HashMultimap.create();
        params.put( "app", "<>" );
        params.put( "widget", "mywidget" );
        params.put( "v", "d41d8cd98f00b204e9800998ecf8427e" );

        final WebRequest webRequest = mock( WebRequest.class );
        when( webRequest.getParams() ).thenReturn( params );

        final IllegalArgumentException ex = assertThrows( IllegalArgumentException.class, () -> instance.handle( webRequest ) );
        assertEquals( "Invalid application key: <>", ex.getMessage() );
    }
}
