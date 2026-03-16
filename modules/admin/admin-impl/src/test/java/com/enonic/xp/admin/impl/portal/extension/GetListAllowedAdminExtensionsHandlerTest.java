package com.enonic.xp.admin.impl.portal.extension;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Multimaps;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.admin.extension.AdminExtensionDescriptor;
import com.enonic.xp.admin.extension.AdminExtensionDescriptorService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.util.GenericValue;
import com.enonic.xp.web.WebResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetListAllowedAdminExtensionsHandlerTest
{
    private AdminExtensionDescriptorService descriptorService;

    private LocaleService localeService;

    private AdminExtensionIconResolver adminExtensionIconResolver;

    private GetListAllowedAdminExtensionsHandler instance;

    @BeforeEach
    void setUp()
    {
        descriptorService = mock( AdminExtensionDescriptorService.class );
        localeService = mock( LocaleService.class );
        adminExtensionIconResolver = mock( AdminExtensionIconResolver.class );

        instance = new GetListAllowedAdminExtensionsHandler( descriptorService, localeService, adminExtensionIconResolver );
    }

    @Test
    void testHandle()
    {
        final PortalRequest webRequest = mock( PortalRequest.class );
        when( webRequest.getParams() ).thenReturn( Multimaps.forMap( Map.of( "interface", "myInterface" ) ) );

        final Icon icon = mock( Icon.class );
        when( icon.toByteArray() ).thenReturn( new byte[0] );
        when( icon.getMimeType() ).thenReturn( "image/png" );

        final AdminExtensionDescriptor descriptor = AdminExtensionDescriptor.create()
            .key( DescriptorKey.from( "myapp:myextension" ) )
            .description( "description" )
            .descriptionI18nKey( "descriptionI18nKey" )
            .displayName( "displayName" )
            .setIcon( icon )
            .interfaces( "myInterface" )
            .config( GenericValue.newObject()
                         .put( "k1", "v1" )
                         .put( "k2", GenericValue.newObject().put( "k21", "v21" ).build() )
                         .put( "k3", GenericValue.newList().add( GenericValue.stringValue( "string" ) ).build() )
                         .put( "k4", GenericValue.booleanValue( true ) )
                         .put( "k5", GenericValue.numberValue( 1.5 ) )
                         .put( "k6", GenericValue.numberValue( 1L ) )
                         .put( "k7", GenericValue.numberValue( 1 ) )
                         .build() )
            .build();

        when( descriptorService.getByInterfaces( anyString() ) ).thenReturn( Descriptors.from( descriptor ) );
        when( localeService.getSupportedLocale( anyList(), any( ApplicationKey.class ) ) ).thenReturn( Locale.ENGLISH );

        when( adminExtensionIconResolver.resolve( eq( descriptor ) ) ).thenReturn( descriptor.getIcon() );

        MessageBundle bundle = mock( MessageBundle.class );
        when( bundle.localize( "descriptionI18nKey" ) ).thenReturn( "localizedDescription" );
        when( localeService.getBundle( eq( descriptor.getApplicationKey() ), any( Locale.class ) ) ).thenReturn( bundle );

        HttpServletRequest httpServletRequest = mock( HttpServletRequest.class );
        when( httpServletRequest.getLocales() ).thenReturn( Collections.enumeration( List.of( Locale.ENGLISH ) ) );

        final WebResponse response;
        when( webRequest.getRawRequest() ).thenReturn( httpServletRequest );
        response = instance.handle( webRequest );

        assertNotNull( response );

        assertInstanceOf( List.class, response.getBody() );

        final List<ObjectNode> body = (List<ObjectNode>) response.getBody();
        assertEquals( 1, body.size() );

        final ObjectNode objectNode = body.get( 0 );
        assertEquals( "myapp:myextension", objectNode.get( "key" ).asText() );
        assertEquals( "?icon&app=myapp&extension=myextension&v=z4PhNX7vuL3xVChQ1m2ABw", objectNode.get( "iconUrl" ).asText() );
        assertEquals( "myapp:myextension", objectNode.get( "url" ).asText() );
        assertEquals( "localizedDescription", objectNode.get( "description" ).asText() );
        assertEquals( "myInterface", objectNode.get( "interfaces" ).get( 0 ).asText() );

        final JsonNode config = objectNode.get( "config" );
        assertEquals( "v1", config.get( "k1" ).asText() );
        assertEquals( "v21", config.get( "k2" ).get( "k21" ).asText() );
        assertEquals( "string", config.get( "k3" ).get( 0 ).asText() );
        assertTrue( config.get( "k4" ).booleanValue() );
        assertEquals( 1.5, config.get( "k5" ).doubleValue() );
        assertEquals( 1L, config.get( "k6" ).longValue() );
        assertEquals( 1, config.get( "k7" ).intValue() );
    }
}
