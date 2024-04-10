package com.enonic.xp.portal.impl.handler.api;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.portal.impl.api.ApiConfig;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.server.RunMode;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandlerChain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ApiHandlerTest
{
    private ApiHandler handler;

    private ApplicationService applicationService;

    private ResourceService resourceService;

    private ApiConfig apiConfig;

    @BeforeEach
    public void setUp()
    {
        this.applicationService = mock( ApplicationService.class );
        this.resourceService = mock( ResourceService.class );

        this.handler = new ApiHandler( this.applicationService, this.resourceService );
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
        Application application = mock( Application.class );
        when( application.getKey() ).thenReturn( ApplicationKey.from( "myapplication" ) );

        when( this.applicationService.getInstalledApplications() ).then( invocationOnMock -> Applications.from( application ) );

        Resource resource = mock( Resource.class );
        when( resource.exists() ).thenReturn( true );
        when( this.resourceService.getResource( ResourceKey.from( ApplicationKey.from( "myapplication" ), "api/api.js" ) ) ).thenReturn(
            resource );

        WebResponse webResponse =
            this.handler.doHandle( mock( WebRequest.class ), mock( WebResponse.class ), mock( WebHandlerChain.class ) );

        assertEquals( HttpStatus.OK, webResponse.getStatus() );
        final Object body = webResponse.getBody();
        assertTrue( body instanceof Map );
        final Map<String, List<String>> objectAsMap = (Map<String, List<String>>) body;
        assertTrue( objectAsMap.containsKey( "resources" ) );
        assertTrue( objectAsMap.get( "resources" ).contains( "myapplication" ) );
        assertTrue( objectAsMap.get( "resources" ).contains( "media" ) );
    }

}
