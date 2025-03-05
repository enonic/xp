package com.enonic.xp.admin.impl.portal.widget;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.admin.widget.WidgetDescriptorService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.servlet.ServletRequestHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetListAllowedWidgetsHandlerTest
{
    private WidgetDescriptorService widgetDescriptorService;

    private LocaleService localeService;

    private WidgetIconResolver widgetIconResolver;

    private GetListAllowedWidgetsHandler instance;

    @BeforeEach
    void setUp()
    {
        widgetDescriptorService = mock( WidgetDescriptorService.class );
        localeService = mock( LocaleService.class );
        widgetIconResolver = mock( WidgetIconResolver.class );

        instance = new GetListAllowedWidgetsHandler( widgetDescriptorService, localeService, widgetIconResolver );
    }

    @Test
    void testHandle()
    {
        final Multimap<String, String> params = HashMultimap.create();
        params.put( "widgetInterface", "myInterface" );

        final PortalRequest webRequest = mock( PortalRequest.class );
        when( webRequest.getParams() ).thenReturn( params );

        final Icon icon = mock( Icon.class );
        when( icon.toByteArray() ).thenReturn( new byte[0] );
        when( icon.getMimeType() ).thenReturn( "image/png" );

        final WidgetDescriptor widgetDescriptor = WidgetDescriptor.create()
            .key( DescriptorKey.from( "myapp:mywidget" ) )
            .description( "description" )
            .descriptionI18nKey( "descriptionI18nKey" )
            .displayName( "displayName" )
            .setIcon( icon )
            .addProperty( "k", "v" )
            .addInterface( "myInterface" )
            .build();

        when( widgetDescriptorService.getByInterfaces( anyString() ) ).thenReturn( Descriptors.from( widgetDescriptor ) );
        when( localeService.getSupportedLocale( anyList(), any( ApplicationKey.class ) ) ).thenReturn( Locale.ENGLISH );

        when( widgetIconResolver.resolve( eq( widgetDescriptor ) ) ).thenReturn( widgetDescriptor.getIcon() );

        MessageBundle bundle = mock( MessageBundle.class );
        when( bundle.localize( "descriptionI18nKey" ) ).thenReturn( "localizedDescription" );
        when( localeService.getBundle( eq( widgetDescriptor.getApplicationKey() ), any( Locale.class ) ) ).thenReturn( bundle );

        HttpServletRequest httpServletRequest = mock( HttpServletRequest.class );
        when( httpServletRequest.getLocales() ).thenReturn( Collections.enumeration( List.of( Locale.ENGLISH ) ) );

        final WebResponse response;
        when( webRequest.getRawRequest() ).thenReturn( httpServletRequest );
        try
        {
            ServletRequestHolder.setRequest( httpServletRequest );
            response = instance.handle( webRequest );
        }
        finally
        {
            ServletRequestHolder.setRequest( null );
        }

        assertNotNull( response );

        assertInstanceOf( List.class, response.getBody() );

        final List<ObjectNode> body = (List<ObjectNode>) response.getBody();
        assertEquals( 1, body.size() );

        final ObjectNode objectNode = body.get( 0 );
        assertEquals( "myapp:mywidget", objectNode.get( "key" ).asText() );
        assertEquals( "?icon&app=myapp&widget=mywidget&v=d41d8cd98f00b204e9800998ecf8427e", objectNode.get( "iconUrl" ).asText() );
        assertEquals( "myapp/mywidget", objectNode.get( "url" ).asText() );
        assertEquals( "localizedDescription", objectNode.get( "description" ).asText() );
        assertEquals( "myInterface", objectNode.get( "interfaces" ).get( 0 ).asText() );
        assertEquals( "v", objectNode.get( "config" ).get( "k" ).asText() );
    }
}
