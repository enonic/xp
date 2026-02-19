package com.enonic.xp.portal.impl.handler.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.api.ApiDescriptor;
import com.enonic.xp.api.ApiDescriptorService;
import com.enonic.xp.api.ApiDescriptors;
import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.portal.impl.api.ApiConfig;
import com.enonic.xp.portal.impl.api.DynamicUniversalApiHandlerRegistry;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.server.RunMode;
import com.enonic.xp.server.RunModeSupport;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandlerChain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApiIndexHandlerTest
{
    private ApiIndexHandler handler;

    private ApplicationService applicationService;

    private ApiDescriptorService apiDescriptorService;

    private DynamicUniversalApiHandlerRegistry universalApiHandlerRegistry;

    private ApiConfig apiConfig;

    @BeforeEach
    void setUp()
    {
        this.applicationService = mock( ApplicationService.class );
        this.apiDescriptorService = mock( ApiDescriptorService.class );
        this.universalApiHandlerRegistry = new DynamicUniversalApiHandlerRegistry();

        this.handler = new ApiIndexHandler( this.applicationService, this.apiDescriptorService, this.universalApiHandlerRegistry );
        this.apiConfig = mock( ApiConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        this.handler.activate( apiConfig );
    }

    @Test
    void testOrder()
    {
        assertEquals( -49, this.handler.getOrder() );
    }

    @Test
    void testCanHandle_dev()
    {
        RunModeSupport.set( RunMode.DEV );

        final WebRequest webRequest1 = new WebRequest();
        webRequest1.setRawPath( "/api" );
        assertTrue( this.handler.canHandle( webRequest1 ) );

        final WebRequest webRequest2 = new WebRequest();
        webRequest2.setRawPath( "/admin/api" );
        assertFalse( this.handler.canHandle( webRequest2 ) );

        final WebRequest webRequest3 = new WebRequest();
        webRequest3.setRawPath( "/adm/api" );
        assertFalse( this.handler.canHandle( webRequest3 ) );

        final WebRequest webRequest4 = new WebRequest();
        webRequest4.setRawPath( "/api/" );
        assertTrue( this.handler.canHandle( webRequest4 ) );

        final WebRequest webRequest5 = new WebRequest();
        webRequest5.setRawPath( "/path" );
        assertFalse( this.handler.canHandle( webRequest5 ) );

        when( apiConfig.api_index_enabled() ).thenReturn( "on" );
        this.handler.activate( apiConfig );
        final WebRequest webRequest6 = new WebRequest();
        webRequest6.setRawPath( "/api" );
        assertTrue( this.handler.canHandle( webRequest6 ) );
    }

    @Test
    void testCanHandle_prod()
    {
        RunModeSupport.set( RunMode.PROD );

        RunModeSupport.set( RunMode.PROD );

        final WebRequest webRequest7 = new WebRequest();
        webRequest7.setRawPath( "/api" );
        assertFalse( this.handler.canHandle( webRequest7 ) );

        when( apiConfig.api_index_enabled() ).thenReturn( "off" );
        this.handler.activate( apiConfig );
        final WebRequest webRequest8 = new WebRequest();
        webRequest8.setRawPath( "/api" );
        assertFalse( this.handler.canHandle( webRequest8 ) );
    }

    @Test
    void testDoHandle()
        throws Exception
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapplication" );
        final Application application = mock( Application.class );

        when( application.getKey() ).thenReturn( applicationKey );
        when( this.applicationService.getInstalledApplications() ).then( invocationOnMock -> Applications.from( application ) );

        final ApiDescriptors apiDescriptors = ApiDescriptors.from( ApiDescriptor.create()
                                                                       .key( DescriptorKey.from( applicationKey, "myapi" ) )
                                                                       .allowedPrincipals( PrincipalKeys.from( RoleKeys.EVERYONE ) )
                                                                       .build(), ApiDescriptor.create()
                                                                       .key( DescriptorKey.from( applicationKey, "myapi2" ) )
                                                                       .allowedPrincipals( PrincipalKeys.from( RoleKeys.EVERYONE ) )
                                                                       .mount( "xp" )
                                                                       .build() );

        when( this.apiDescriptorService.getByApplication( eq( applicationKey ) ) ).thenReturn( apiDescriptors );

        universalApiHandlerRegistry.addApiHandler( request -> WebResponse.create().build(),
                                                   Map.of( "key", "admin:extension", "displayName", "Display Name", "description",
                                                           "Brief description", "documentationUrl", "https://docs.enonic.com", "mount",
                                                           new String[]{"xp", "management"}, "allowedPrincipals",
                                                           RoleKeys.EVERYONE.toString() ) );

        universalApiHandlerRegistry.addApiHandler( request -> WebResponse.create().build(),
                                                   Map.of( "key", "admin:event", "displayName", "Event API", "description", "Event API",
                                                           "documentationUrl", "https://docs.enonic.com", "allowedPrincipals",
                                                           RoleKeys.ADMIN_LOGIN.toString() ) );

        WebResponse webResponse = this.handler.doHandle( new WebRequest(), WebResponse.create().build(), mock( WebHandlerChain.class ) );

        assertEquals( HttpStatus.OK, webResponse.getStatus() );
        final Object body = webResponse.getBody();
        assertInstanceOf( Map.class, body );

        final Map<String, List<Map<String, Object>>> objectAsMap = (Map<String, List<Map<String, Object>>>) body;
        assertTrue( objectAsMap.containsKey( "resources" ) );

        final List<Map<String, Object>> resources = objectAsMap.get( "resources" );
        assertEquals( 2, resources.size() );

        final Map<String, Object> dynamicApiResource = resources.get( 0 );

        assertEquals( "admin:extension", dynamicApiResource.get( "descriptor" ) );
        assertEquals( "admin", dynamicApiResource.get( "application" ) );
        assertEquals( "extension", dynamicApiResource.get( "name" ) );
        assertEquals( "Display Name", dynamicApiResource.get( "displayName" ) );
        assertEquals( "Brief description", dynamicApiResource.get( "description" ) );
        assertEquals( "https://docs.enonic.com", dynamicApiResource.get( "documentationUrl" ) );
        assertEquals( Set.of( "management", "xp" ), dynamicApiResource.get( "mount" ) );

        final Map<String, Object> apiResource = resources.get( 1 );

        assertEquals( "myapplication:myapi2", apiResource.get( "descriptor" ) );
        assertEquals( "myapplication", apiResource.get( "application" ) );
        assertEquals( "myapi2", apiResource.get( "name" ) );
        assertEquals( List.of( RoleKeys.EVERYONE.toString() ), apiResource.get( "allowedPrincipals" ) );
    }

}
