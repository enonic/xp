package com.enonic.xp.lib.admin;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.admin.event.AdminEventHub;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.url.ApiUrlParams;
import com.enonic.xp.portal.url.GenerateUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LibAdminTest
    extends ScriptTestSupport
{

    private PortalUrlService portalUrlService;

    private AdminEventHub adminEventHub;

    @Override
    protected void initialize()
        throws Exception
    {
        super.initialize();

        portalUrlService = mock( PortalUrlService.class );
        addService( PortalUrlService.class, portalUrlService );

        adminEventHub = mock( AdminEventHub.class );
        addService( AdminEventHub.class, adminEventHub );
    }

    @Test
    void createTopic()
    {
        when( adminEventHub.registerTopic( any(), any(), any() ) ).thenReturn( "myapplication:myTopic" );

        runFunction( "/test/admin-test.js", "createTopic" );

        ArgumentCaptor<PrincipalKeys> allow = ArgumentCaptor.forClass( PrincipalKeys.class );
        ArgumentCaptor<ApplicationKey> owner = ArgumentCaptor.forClass( ApplicationKey.class );
        verify( adminEventHub ).registerTopic( owner.capture(), eq( "myTopic" ), allow.capture() );

        assertEquals( PrincipalKeys.from( "role:system.admin.login" ), allow.getValue() );
        assertEquals( "myapplication", owner.getValue().toString() );
    }

    @Test
    void createTopicWithoutAllow()
    {
        when( adminEventHub.registerTopic( any(), any(), any() ) ).thenReturn( "myapplication:myTopic" );

        runFunction( "/test/admin-test.js", "createTopicWithoutAllow" );

        ArgumentCaptor<PrincipalKeys> allow = ArgumentCaptor.forClass( PrincipalKeys.class );
        verify( adminEventHub ).registerTopic( any(), eq( "myTopic" ), allow.capture() );

        assertEquals( PrincipalKeys.empty(), allow.getValue() );
    }

    @Test
    void sendToTopic()
    {
        runFunction( "/test/admin-test.js", "sendToTopic" );

        ArgumentCaptor<ApplicationKey> caller = ArgumentCaptor.forClass( ApplicationKey.class );
        ArgumentCaptor<Map<String, ?>> message = ArgumentCaptor.forClass( Map.class );
        verify( adminEventHub ).publish( caller.capture(), eq( "myTopic" ), message.capture() );

        assertEquals( "myapplication", caller.getValue().toString() );
        assertEquals( 42.0, ( (Number) message.getValue().get( "count" ) ).doubleValue() );
    }

    @Test
    void sendToTopicWithoutMessage()
    {
        runFunction( "/test/admin-test.js", "sendToTopicWithoutMessage" );

        ArgumentCaptor<Map<String, ?>> message = ArgumentCaptor.forClass( Map.class );
        verify( adminEventHub ).publish( any(), eq( "myTopic" ), message.capture() );

        assertEquals( Map.of(), message.getValue() );
    }

    @Test
    void testGetToolUrl()
    {
        when( portalUrlService.generateUrl( any( GenerateUrlParams.class ) ) ).thenReturn( "generated_url" );

        runFunction( "/test/admin-test.js", "testGetToolUrl" );

        ArgumentCaptor<GenerateUrlParams> captor = ArgumentCaptor.forClass( GenerateUrlParams.class );
        verify( portalUrlService ).generateUrl( captor.capture() );

        GenerateUrlParams params = captor.getValue();

        assertEquals( "server", params.getType() );
        assertEquals( "/admin/myapp/mytool", params.getPath() );
    }

    @Test
    void getHomeToolUrl()
    {
        when( portalUrlService.generateUrl( any( GenerateUrlParams.class ) ) ).thenReturn( "generated_url" );

        runFunction( "/test/admin-test.js", "getHomeToolUrl" );

        ArgumentCaptor<GenerateUrlParams> captor = ArgumentCaptor.forClass( GenerateUrlParams.class );
        verify( portalUrlService ).generateUrl( captor.capture() );

        GenerateUrlParams params = captor.getValue();

        assertEquals( "absolute", params.getType() );
        assertEquals( "/admin", params.getPath() );
    }

    @Test
    void testWidgetUrl()
    {
        when( portalUrlService.apiUrl( any( ApiUrlParams.class ) ) ).thenReturn( "generated_url" );

        runFunction( "/test/admin-test.js", "testWidgetUrl" );

        ArgumentCaptor<ApiUrlParams> captor = ArgumentCaptor.forClass( ApiUrlParams.class );
        verify( portalUrlService ).apiUrl( captor.capture() );

        ApiUrlParams params = captor.getValue();

        assertEquals( "server", params.getType() );

        assertEquals( "admin:extension", params.getApi().toString() );

        List<String> pathSegments = params.getPathSegments();
        assertEquals( 1, pathSegments.size() );
        assertEquals( "myapp:mywidget", pathSegments.get( 0 ) );

        Map<String, List<String>> queryParams = params.getQueryParams();

        assertEquals( 2, queryParams.size() );
        assertEquals( "v1", queryParams.get( "k1" ).iterator().next() );

        Iterator<String> k2 = queryParams.get( "k2" ).iterator();
        assertEquals( "v21", k2.next() );
        assertEquals( "v22", k2.next() );
    }

    @Test
    void testExtensionUrl()
    {
        when( portalUrlService.apiUrl( any( ApiUrlParams.class ) ) ).thenReturn( "generated_url" );

        runFunction( "/test/admin-test.js", "testExtensionUrl" );

        ArgumentCaptor<ApiUrlParams> captor = ArgumentCaptor.forClass( ApiUrlParams.class );
        verify( portalUrlService ).apiUrl( captor.capture() );

        ApiUrlParams params = captor.getValue();

        assertEquals( "server", params.getType() );

        assertEquals( "admin:extension", params.getApi().toString() );

        List<String> pathSegments = params.getPathSegments();
        assertEquals( 1, pathSegments.size() );
        assertEquals( "myapp:myExtension", pathSegments.get( 0 ) );

        Map<String, List<String>> queryParams = params.getQueryParams();

        assertEquals( 2, queryParams.size() );
        assertEquals( "v1", queryParams.get( "k1" ).iterator().next() );

        Iterator<String> k2 = queryParams.get( "k2" ).iterator();
        assertEquals( "v21", k2.next() );
        assertEquals( "v22", k2.next() );
    }

    @Test
    void testWidgetUrlWithoutParams()
    {
        when( portalUrlService.apiUrl( any( ApiUrlParams.class ) ) ).thenReturn( "generated_url" );

        runFunction( "/test/admin-test.js", "testWidgetUrlWithoutParams" );

        ArgumentCaptor<ApiUrlParams> captor = ArgumentCaptor.forClass( ApiUrlParams.class );
        verify( portalUrlService ).apiUrl( captor.capture() );

        ApiUrlParams params = captor.getValue();

        assertEquals( "server", params.getType() );

        assertEquals( "admin:extension", params.getApi().toString() );

        List<String> pathSegments = params.getPathSegments();
        assertEquals( 1, pathSegments.size() );
        assertEquals( "myapp:mywidget", pathSegments.get( 0 ) );

        Map<String, List<String>> queryParams = params.getQueryParams();

        assertEquals( 0, queryParams.size() );
    }
}
