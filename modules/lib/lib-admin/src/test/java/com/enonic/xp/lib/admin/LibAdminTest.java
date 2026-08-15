package com.enonic.xp.lib.admin;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.admin.event.AdminEventHub;
import com.enonic.xp.admin.event.PublishMessageParams;
import com.enonic.xp.admin.event.SetTopicParams;
import com.enonic.xp.portal.url.ApiUrlParams;
import com.enonic.xp.portal.url.GenerateUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.testing.ScriptTestSupport;
import com.enonic.xp.util.GenericValue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
    void setTopic()
    {
        when( adminEventHub.setTopic( any() ) ).thenReturn( "myapplication:myTopic" );

        runFunction( "/test/admin-test.js", "setTopic" );

        ArgumentCaptor<SetTopicParams> params = ArgumentCaptor.forClass( SetTopicParams.class );
        verify( adminEventHub ).setTopic( params.capture() );

        assertEquals( "myapplication", params.getValue().getOwner().toString() );
        assertEquals( "myTopic", params.getValue().getName() );
        assertEquals( PrincipalKeys.from( "role:system.admin.login" ), params.getValue().getAllow() );
    }

    @Test
    void setTopicWithSingleAllow()
    {
        when( adminEventHub.setTopic( any() ) ).thenReturn( "myapplication:myTopic" );

        runFunction( "/test/admin-test.js", "setTopicWithSingleAllow" );

        ArgumentCaptor<SetTopicParams> params = ArgumentCaptor.forClass( SetTopicParams.class );
        verify( adminEventHub ).setTopic( params.capture() );

        assertEquals( PrincipalKeys.from( "role:system.admin.login" ), params.getValue().getAllow() );
    }

    @Test
    void setTopicWithEmptyAllow()
    {
        when( adminEventHub.setTopic( any() ) ).thenReturn( "myapplication:myTopic" );

        runFunction( "/test/admin-test.js", "setTopicWithEmptyAllow" );

        ArgumentCaptor<SetTopicParams> params = ArgumentCaptor.forClass( SetTopicParams.class );
        verify( adminEventHub ).setTopic( params.capture() );

        assertEquals( PrincipalKeys.empty(), params.getValue().getAllow() );
    }

    @Test
    void setTopicWithoutAllow()
    {
        assertThrows( RuntimeException.class, () -> runFunction( "/test/admin-test.js", "setTopicWithoutAllow" ) );

        verify( adminEventHub, never() ).setTopic( any() );
    }

    @Test
    void sendToTopic()
    {
        runFunction( "/test/admin-test.js", "sendToTopic" );

        ArgumentCaptor<PublishMessageParams> params = ArgumentCaptor.forClass( PublishMessageParams.class );
        verify( adminEventHub ).publish( params.capture() );

        assertEquals( "myapplication", params.getValue().getCaller().toString() );
        assertEquals( "myTopic", params.getValue().getName() );
        assertEquals( 42, params.getValue().getMessage().property( "count" ).asInteger() );
    }

    @Test
    void sendToTopicWithoutMessage()
    {
        runFunction( "/test/admin-test.js", "sendToTopicWithoutMessage" );

        ArgumentCaptor<PublishMessageParams> params = ArgumentCaptor.forClass( PublishMessageParams.class );
        verify( adminEventHub ).publish( params.capture() );

        assertEquals( GenericValue.newObject().build(), params.getValue().getMessage() );
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
