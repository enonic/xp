package com.enonic.xp.portal.impl.auth;

import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.auth.AuthDescriptor;
import com.enonic.xp.auth.AuthDescriptorService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.auth.AuthControllerExecutionParams;
import com.enonic.xp.portal.impl.controller.AbstractControllerTest;
import com.enonic.xp.portal.impl.script.PortalScriptServiceImpl;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.script.impl.ScriptRuntimeFactoryImpl;
import com.enonic.xp.security.AuthConfig;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UserStore;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

public class AuthControllerServiceImplTest
{
    private AuthControllerServiceImpl authControllerService;

    @Before
    public void setup()
        throws Exception
    {
        //Mocks the AuthDescriptorService
        final AuthDescriptorService authDescriptorService = Mockito.mock( AuthDescriptorService.class );
        Mockito.when( authDescriptorService.getDescriptor( ApplicationKey.from( "myapplication" ) ) ).thenReturn(
            AuthDescriptor.create().key( ApplicationKey.from( "myapplication" ) ).build() );

        //Mocks the SecurityService
        final SecurityService securityService = Mockito.mock( SecurityService.class );
        final UserStore emptyUserStore = UserStore.create().build();
        final AuthConfig authConfig = AuthConfig.create().applicationKey( ApplicationKey.from( "myapplication" ) ).build();
        final UserStore userStore = UserStore.create().authConfig( authConfig ).build();
        Mockito.when( securityService.getUserStore( UserStoreKey.from( "myemptyuserstore" ) ) ).thenReturn( emptyUserStore );
        Mockito.when( securityService.getUserStore( UserStoreKey.from( "myuserstore" ) ) ).thenReturn( userStore );

        //Mocks the PortalScriptService
        final PortalScriptService portalScriptService = setupPortalScriptService();

        //Creates AuthControllerScriptFactoryImpl
        final AuthControllerScriptFactoryImpl authControllerScriptFactory = new AuthControllerScriptFactoryImpl();
        authControllerScriptFactory.setScriptService( portalScriptService );

        //Creates AuthControllerServiceImpl
        authControllerService = new AuthControllerServiceImpl();
        authControllerService.setAuthControllerScriptFactory( authControllerScriptFactory );
        authControllerService.setAuthDescriptorService( authDescriptorService );
        authControllerService.setSecurityService( securityService );
    }

    private PortalScriptService setupPortalScriptService()
    {
        final BundleContext bundleContext = Mockito.mock( BundleContext.class );

        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getBundleContext() ).thenReturn( bundleContext );

        final Application application = Mockito.mock( Application.class );
        Mockito.when( application.getBundle() ).thenReturn( bundle );
        Mockito.when( application.getClassLoader() ).thenReturn( getClass().getClassLoader() );

        final ApplicationService applicationService = Mockito.mock( ApplicationService.class );
        Mockito.when( applicationService.getInstalledApplication( ApplicationKey.from( "defaultapplication" ) ) ).thenReturn( application );
        Mockito.when( applicationService.getInstalledApplication( ApplicationKey.from( "myapplication" ) ) ).thenReturn( application );

        ResourceService resourceService = Mockito.mock( ResourceService.class );
        Mockito.when( resourceService.getResource( Mockito.any() ) ).thenAnswer( invocation -> {
            final ResourceKey resourceKey = (ResourceKey) invocation.getArguments()[0];
            final URL resourceUrl =
                AbstractControllerTest.class.getResource( "/" + resourceKey.getApplicationKey() + resourceKey.getPath() );
            return new UrlResource( resourceKey, resourceUrl );
        } );

        final ScriptRuntimeFactoryImpl runtimeFactory = new ScriptRuntimeFactoryImpl();
        runtimeFactory.setApplicationService( applicationService );
        runtimeFactory.setResourceService( resourceService );

        final PortalScriptServiceImpl scriptService = new PortalScriptServiceImpl();
        scriptService.setScriptRuntimeFactory( runtimeFactory );
        scriptService.initialize();

        return scriptService;
    }

    @Test
    public void executeMissingUserStore()
        throws IOException
    {
        final AuthControllerExecutionParams executionParams = AuthControllerExecutionParams.create().
            portalRequest( new PortalRequest() ).
            userStoreKey( UserStoreKey.from( "missinguserstore" ) ).
            functionName( "missingfunction" ).
            build();
        final PortalResponse portalResponse = authControllerService.execute( executionParams );
        Assert.assertNull( portalResponse );
    }

    @Test
    public void executeMissingFunction()
        throws IOException
    {
        final AuthControllerExecutionParams executionParams = AuthControllerExecutionParams.create().
            portalRequest( new PortalRequest() ).
            userStoreKey( UserStoreKey.from( "myemptyuserstore" ) ).
            functionName( "missingfunction" ).
            build();
        final PortalResponse portalResponse = authControllerService.execute( executionParams );
        Assert.assertNull( portalResponse );
    }

    @Test
    public void executeUserStoreWithoutIdProvider()
        throws IOException
    {
        final AuthControllerExecutionParams executionParams = AuthControllerExecutionParams.create().
            portalRequest( new PortalRequest() ).
            userStoreKey( UserStoreKey.from( "myemptyuserstore" ) ).
            functionName( "myfunction" ).
            build();
        final PortalResponse portalResponse = authControllerService.execute( executionParams );
        Assert.assertNull( portalResponse );
    }

    @Test
    public void execute()
        throws IOException
    {
        final AuthControllerExecutionParams executionParams = AuthControllerExecutionParams.create().
            portalRequest( new PortalRequest() ).
            userStoreKey( UserStoreKey.from( "myuserstore" ) ).
            functionName( "myfunction" ).
            build();
        final PortalResponse portalResponse = authControllerService.execute( executionParams );
        Assert.assertNotNull( portalResponse );
        Assert.assertEquals( HttpStatus.OK, portalResponse.getStatus() );
        Assert.assertEquals( "myapplication/myfunction", portalResponse.getBody() );
    }


    @Test
    public void executeWithoutVirtualHost()
        throws IOException
    {
        final HttpServletRequest httpServletRequest = createHttpServletRequest();
        final AuthControllerExecutionParams executionParams = AuthControllerExecutionParams.create().
            servletRequest( httpServletRequest ).
            functionName( "myfunction" ).
            build();
        final PortalResponse portalResponse = authControllerService.execute( executionParams );
        Assert.assertNull( portalResponse );
    }


    @Test
    public void executeWithVirtualHost()
        throws IOException
    {
        final HttpServletRequest httpServletRequest = createHttpServletRequest();

        final VirtualHost virtualHost = Mockito.mock( VirtualHost.class );
        Mockito.when( virtualHost.getUserStoreKey() ).thenReturn( UserStoreKey.from( "myuserstore" ) );
        Mockito.when( httpServletRequest.getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        VirtualHostHelper.setVirtualHost( httpServletRequest, virtualHost );

        final AuthControllerExecutionParams executionParams = AuthControllerExecutionParams.create().
            servletRequest( httpServletRequest ).
            functionName( "myfunction" ).
            build();
        final PortalResponse portalResponse = authControllerService.execute( executionParams );
        Assert.assertNotNull( portalResponse );
        Assert.assertEquals( HttpStatus.OK, portalResponse.getStatus() );
        Assert.assertEquals( "myapplication/myfunction", portalResponse.getBody() );
    }

    private HttpServletRequest createHttpServletRequest()
    {
        final HttpServletRequest httpServletRequest = Mockito.mock( HttpServletRequest.class );
        Mockito.when( httpServletRequest.getMethod() ).thenReturn( "GET" );
        Mockito.when( httpServletRequest.getScheme() ).thenReturn( "http" );
        Mockito.when( httpServletRequest.getServerName() ).thenReturn( "localhost" );
        Mockito.when( httpServletRequest.getLocalPort() ).thenReturn( 80 );
        Mockito.when( httpServletRequest.getRequestURI() ).thenReturn( "/admin/tool" );
        Mockito.when( httpServletRequest.getHeaderNames() ).thenReturn( new Vector<String>().elements() );
        return httpServletRequest;
    }
}
