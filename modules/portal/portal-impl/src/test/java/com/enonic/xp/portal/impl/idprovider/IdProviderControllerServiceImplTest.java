package com.enonic.xp.portal.impl.idprovider;

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
import com.enonic.xp.idprovider.IdProviderDescriptor;
import com.enonic.xp.idprovider.IdProviderDescriptorService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.idprovider.IdProviderControllerExecutionParams;
import com.enonic.xp.portal.impl.controller.AbstractControllerTest;
import com.enonic.xp.portal.impl.script.PortalScriptServiceImpl;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.script.impl.standard.ScriptRuntimeFactoryImpl;
import com.enonic.xp.security.IdProvider;
import com.enonic.xp.security.IdProviderConfig;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

public class IdProviderControllerServiceImplTest
{
    private IdProviderControllerServiceImpl idProviderControllerService;

    @Before
    public void setup()
        throws Exception
    {
        //Mocks the IdProviderDescriptorService
        final IdProviderDescriptorService idProviderDescriptorService = Mockito.mock( IdProviderDescriptorService.class );
        Mockito.when( idProviderDescriptorService.getDescriptor( ApplicationKey.from( "myapplication" ) ) ).thenReturn(
            IdProviderDescriptor.create().key( ApplicationKey.from( "myapplication" ) ).build() );

        //Mocks the SecurityService
        final SecurityService securityService = Mockito.mock( SecurityService.class );
        final IdProvider emptyIdProvider = IdProvider.create().build();
        final IdProviderConfig idProviderConfig =
            IdProviderConfig.create().applicationKey( ApplicationKey.from( "myapplication" ) ).build();
        final IdProvider idProvider = IdProvider.create().idProviderConfig( idProviderConfig ).build();

        Mockito.when( securityService.getIdProvider( IdProviderKey.from( "myemptyidprovider" ) ) ).thenReturn( emptyIdProvider );
        Mockito.when( securityService.getIdProvider( IdProviderKey.from( "myidprovider" ) ) ).thenReturn( idProvider );

        Mockito.when( securityService.getIdProvider( IdProviderKey.from( "myemptyuserstore" ) ) ).thenReturn( emptyIdProvider );
        Mockito.when( securityService.getIdProvider( IdProviderKey.from( "myuserstore" ) ) ).thenReturn( idProvider );

        //Mocks the PortalScriptService
        final PortalScriptService portalScriptService = setupPortalScriptService();

        //Creates IdProviderControllerScriptFactoryImpl
        final IdProviderControllerScriptFactoryImpl idProviderControllerScriptFactory = new IdProviderControllerScriptFactoryImpl();
        idProviderControllerScriptFactory.setScriptService( portalScriptService );

        //Creates IdProviderControllerServiceImpl
        idProviderControllerService = new IdProviderControllerServiceImpl();
        idProviderControllerService.setIdProviderControllerScriptFactory( idProviderControllerScriptFactory );
        idProviderControllerService.setIdProviderDescriptorService( idProviderDescriptorService );
        idProviderControllerService.setSecurityService( securityService );
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
    public void executeMissingIdProvider()
        throws IOException
    {
        final IdProviderControllerExecutionParams executionParams = IdProviderControllerExecutionParams.create().
            portalRequest( new PortalRequest() ).
            idProviderKey( IdProviderKey.from( "missingidprovider" ) ).
            functionName( "missingfunction" ).
            build();
        final PortalResponse portalResponse = idProviderControllerService.execute( executionParams );
        Assert.assertNull( portalResponse );
    }

    @Test
    public void executeMissingFunction()
        throws IOException
    {
        final IdProviderControllerExecutionParams executionParams = IdProviderControllerExecutionParams.create().
            portalRequest( new PortalRequest() ).
            idProviderKey( IdProviderKey.from( "myemptyidprovider" ) ).
            functionName( "missingfunction" ).
            build();
        final PortalResponse portalResponse = idProviderControllerService.execute( executionParams );
        Assert.assertNull( portalResponse );
    }

    @Test
    public void executeIdProviderWithoutApplication()
        throws IOException
    {
        final IdProviderControllerExecutionParams executionParams = IdProviderControllerExecutionParams.create().
            portalRequest( new PortalRequest() ).
            idProviderKey( IdProviderKey.from( "myemptyidprovider" ) ).
            functionName( "myfunction" ).
            build();
        final PortalResponse portalResponse = idProviderControllerService.execute( executionParams );
        Assert.assertNull( portalResponse );
    }

    @Test
    public void execute()
        throws IOException
    {
        final IdProviderControllerExecutionParams executionParams = IdProviderControllerExecutionParams.create().
            portalRequest( new PortalRequest() ).
            idProviderKey( IdProviderKey.from( "myidprovider" ) ).
            functionName( "myfunction" ).
            build();
        final PortalResponse portalResponse = idProviderControllerService.execute( executionParams );
        Assert.assertNotNull( portalResponse );
        Assert.assertEquals( HttpStatus.OK, portalResponse.getStatus() );
        Assert.assertEquals( "myapplication/myfunction", portalResponse.getBody() );
    }


    @Test
    public void executeWithoutVirtualHost()
        throws IOException
    {
        final HttpServletRequest httpServletRequest = createHttpServletRequest();
        final IdProviderControllerExecutionParams executionParams = IdProviderControllerExecutionParams.create().
            servletRequest( httpServletRequest ).
            functionName( "myfunction" ).
            build();
        final PortalResponse portalResponse = idProviderControllerService.execute( executionParams );
        Assert.assertNull( portalResponse );
    }


    @Test
    public void executeWithVirtualHost()
        throws IOException
    {
        final HttpServletRequest httpServletRequest = createHttpServletRequest();

        final VirtualHost virtualHost = Mockito.mock( VirtualHost.class );
        Mockito.when( virtualHost.getUserStoreKey() ).thenReturn( IdProviderKey.from( "myuserstore" ) );
        Mockito.when( httpServletRequest.getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        VirtualHostHelper.setVirtualHost( httpServletRequest, virtualHost );

        final IdProviderControllerExecutionParams executionParams = IdProviderControllerExecutionParams.create().
            servletRequest( httpServletRequest ).
            functionName( "myfunction" ).
            build();
        final PortalResponse portalResponse = idProviderControllerService.execute( executionParams );
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
