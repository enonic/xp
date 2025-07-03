package com.enonic.xp.portal.impl.handler.api;

import java.util.List;
import java.util.Map;

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

public class ApiHandlerTest
{
    private ApiHandler handler;

    private ApplicationService applicationService;

    private ApiDescriptorService apiDescriptorService;

    private DynamicUniversalApiHandlerRegistry universalApiHandlerRegistry;

    private ApiConfig apiConfig;

    @BeforeEach
    public void setUp()
    {
        this.applicationService = mock( ApplicationService.class );
        this.apiDescriptorService = mock( ApiDescriptorService.class );
        this.universalApiHandlerRegistry = new DynamicUniversalApiHandlerRegistry();

        this.handler = new ApiHandler( this.applicationService, this.apiDescriptorService, this.universalApiHandlerRegistry );
        this.apiConfig = mock( ApiConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        this.handler.activate( apiConfig );
    }

    @Test
    public void testOrder()
    {
        assertEquals( -49, this.handler.getOrder() );
    }

    @Test
    public void testCanHandle()
    {
        WebRequest webRequest = mock( WebRequest.class );

        // Set run mode to DEV
        System.setProperty( "xp.runMode", RunMode.DEV.name() );

        when( webRequest.getRawPath() ).thenReturn( "/api" );
        assertTrue( this.handler.canHandle( webRequest ) );

        when( webRequest.getRawPath() ).thenReturn( "/admin/api" );
        assertFalse( this.handler.canHandle( webRequest ) );

        when( webRequest.getRawPath() ).thenReturn( "/adm/api" );
        assertFalse( this.handler.canHandle( webRequest ) );

        when( webRequest.getRawPath() ).thenReturn( "/api/" );
        assertFalse( this.handler.canHandle( webRequest ) );

        when( webRequest.getRawPath() ).thenReturn( "/path" );
        assertFalse( this.handler.canHandle( webRequest ) );

        when( apiConfig.api_index_enabled() ).thenReturn( "on" );
        when( webRequest.getRawPath() ).thenReturn( "/api" );
        assertTrue( this.handler.canHandle( webRequest ) );

        // Remove run mode
        System.getProperties().remove( "xp.runMode" );

        when( webRequest.getRawPath() ).thenReturn( "/api" );
        assertFalse( this.handler.canHandle( webRequest ) );

        when( apiConfig.api_index_enabled() ).thenReturn( "off" );
        when( webRequest.getRawPath() ).thenReturn( "/api" );
        assertFalse( this.handler.canHandle( webRequest ) );

        Application welcomeApp = mock( Application.class );
        when( applicationService.get( ApplicationKey.from( "com.enonic.xp.app.welcome" ) ) ).thenReturn( welcomeApp );
        when( apiConfig.api_index_enabled() ).thenReturn( "on" );
        assertTrue( this.handler.canHandle( webRequest ) );
    }

    @Test
    public void testDoHandle()
        throws Exception
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapplication" );
        final Application application = mock( Application.class );

        when( application.getKey() ).thenReturn( applicationKey );
        when( this.applicationService.getInstalledApplications() ).then( invocationOnMock -> Applications.from( application ) );

        final ApiDescriptors apiDescriptors = ApiDescriptors.from( ApiDescriptor.create()
                                                                       .key( DescriptorKey.from( applicationKey, "myapi" ) )
                                                                       .allowedPrincipals( PrincipalKeys.from( RoleKeys.EVERYONE ) )
                                                                       .mount( false )
                                                                       .build(),
                                                                   ApiDescriptor.create()
                                                                       .key( DescriptorKey.from( applicationKey, "myapi2" ) )
                                                                       .allowedPrincipals( PrincipalKeys.from( RoleKeys.EVERYONE ) )
                                                                       .mount( true )
                                                                       .build() );

        when( this.apiDescriptorService.getByApplication( eq( applicationKey ) ) ).thenReturn( apiDescriptors );

        universalApiHandlerRegistry.addApiHandler( request -> WebResponse.create().build(),
                                                   Map.of( "applicationKey", "admin", "apiKey", "widget", "displayName", "Display Name",
                                                           "description", "Brief description", "documentationUrl",
                                                           "https://docs.enonic.com", "mount", "true", "allowedPrincipals",
                                                           RoleKeys.EVERYONE.toString() ) );

        WebResponse webResponse =
            this.handler.doHandle( mock( WebRequest.class ), mock( WebResponse.class ), mock( WebHandlerChain.class ) );

        assertEquals( HttpStatus.OK, webResponse.getStatus() );
        final Object body = webResponse.getBody();
        assertInstanceOf( Map.class, body );

        final Map<String, List<Map<String, Object>>> objectAsMap = (Map<String, List<Map<String, Object>>>) body;
        assertTrue( objectAsMap.containsKey( "resources" ) );

        final List<Map<String, Object>> resources = objectAsMap.get( "resources" );
        assertEquals( 2, resources.size() );

        final Map<String, Object> dynamicApiResource = resources.get( 0 );

        assertEquals( "admin:widget", dynamicApiResource.get( "descriptor" ) );
        assertEquals( "admin", dynamicApiResource.get( "application" ) );
        assertEquals( "widget", dynamicApiResource.get( "name" ) );
        assertEquals( "Display Name", dynamicApiResource.get( "displayName" ) );
        assertEquals( "Brief description", dynamicApiResource.get( "description" ) );
        assertEquals( "https://docs.enonic.com", dynamicApiResource.get( "documentationUrl" ) );
        assertTrue( (boolean) dynamicApiResource.get( "mount" ) );

        final Map<String, Object> apiResource = resources.get( 1 );

        assertEquals( "myapplication:myapi2", apiResource.get( "descriptor" ) );
        assertEquals( "myapplication", apiResource.get( "application" ) );
        assertEquals( "myapi2", apiResource.get( "name" ) );
        assertEquals( List.of( RoleKeys.EVERYONE.toString() ), apiResource.get( "allowedPrincipals" ) );
    }

}
