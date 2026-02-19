package com.enonic.xp.lib.admin;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.portal.url.ApiUrlParams;
import com.enonic.xp.portal.url.GenerateUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LibAdminTest
    extends ScriptTestSupport
{

    private PortalUrlService portalUrlService;

    @Override
    protected void initialize()
        throws Exception
    {
        super.initialize();

        portalUrlService = mock( PortalUrlService.class );
        addService( PortalUrlService.class, portalUrlService );
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

        DescriptorKey descriptorKey = params.getDescriptorKey();
        assertEquals( "admin", descriptorKey.getApplicationKey().getName() );
        assertEquals( "extension", descriptorKey.getName() );

        List<String> pathSegments = params.getPathSegments();
        assertEquals( 1, pathSegments.size() );
        assertEquals( "myapp:mywidget", pathSegments.get( 0 ) );

        Map<String, Collection<String>> queryParams = params.getQueryParams();

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

        DescriptorKey descriptorKey = params.getDescriptorKey();
        assertEquals( "admin", descriptorKey.getApplicationKey().getName() );
        assertEquals( "extension", descriptorKey.getName() );

        List<String> pathSegments = params.getPathSegments();
        assertEquals( 1, pathSegments.size() );
        assertEquals( "myapp:myExtension", pathSegments.get( 0 ) );

        Map<String, Collection<String>> queryParams = params.getQueryParams();

        assertEquals( 2, queryParams.size() );
        assertEquals( "v1", queryParams.get( "k1" ).iterator().next() );

        Iterator<String> k2 = queryParams.get( "k2" ).iterator();
        assertEquals( "v21", k2.next() );
        assertEquals( "v22", k2.next() );
    }

    @Test
    void testGetTools()
    {
        mockAdminToolDescriptorService();
        runFunction( "/test/admin-test.js", "testGetTools" );
    }

    @Test
    void testGetToolsWithLocales()
    {
        mockAdminToolDescriptorService();
        runFunction( "/test/admin-test.js", "testGetToolsWithLocales" );
    }

    private void mockAdminToolDescriptorService()
    {
        AdminToolDescriptorService adminToolDescriptorService = mock( AdminToolDescriptorService.class );
        addService( AdminToolDescriptorService.class, adminToolDescriptorService );

        ApplicationService applicationService = mock( ApplicationService.class );
        addService( ApplicationService.class, applicationService );

        LocaleService localeService = mock( LocaleService.class );
        addService( LocaleService.class, localeService );

        // Add Context binding with authentication info
        final com.enonic.xp.security.User user = com.enonic.xp.security.User.create()
            .key( com.enonic.xp.security.PrincipalKey.ofUser( com.enonic.xp.security.IdProviderKey.system(), "testuser" ) )
            .login( "testuser" )
            .build();
        final com.enonic.xp.security.auth.AuthenticationInfo authInfo =
            com.enonic.xp.security.auth.AuthenticationInfo.create().user( user ).principals( com.enonic.xp.security.RoleKeys.ADMIN ).build();
        final com.enonic.xp.context.Context context = com.enonic.xp.context.ContextBuilder.create().authInfo( authInfo ).build();
        addBinding( com.enonic.xp.context.Context.class, context );

        com.enonic.xp.admin.tool.AdminToolDescriptors emptyDescriptors =
            com.enonic.xp.admin.tool.AdminToolDescriptors.empty();
        when( adminToolDescriptorService.getAllowedAdminToolDescriptors( any() ) ).thenReturn( emptyDescriptors );
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

        DescriptorKey descriptorKey = params.getDescriptorKey();
        assertEquals( "admin", descriptorKey.getApplicationKey().getName() );
        assertEquals( "extension", descriptorKey.getName() );

        List<String> pathSegments = params.getPathSegments();
        assertEquals( 1, pathSegments.size() );
        assertEquals( "myapp:mywidget", pathSegments.get( 0 ) );

        Map<String, Collection<String>> queryParams = params.getQueryParams();

        assertEquals( 0, queryParams.size() );
    }
}
