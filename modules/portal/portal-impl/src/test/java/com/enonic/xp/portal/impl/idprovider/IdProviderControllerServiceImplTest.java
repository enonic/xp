package com.enonic.xp.portal.impl.idprovider;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.config.ConfigBuilder;
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
import com.enonic.xp.script.ScriptFixturesFacade;
import com.enonic.xp.script.runtime.ScriptRuntimeFactory;
import com.enonic.xp.security.IdProvider;
import com.enonic.xp.security.IdProviderConfig;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.util.Version;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.impl.serializer.WebSerializerServiceImpl;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IdProviderControllerServiceImplTest
{
    private IdProviderControllerServiceImpl idProviderControllerService;

    @BeforeEach
    void setup()
    {
        //Mocks the IdProviderDescriptorService
        final IdProviderDescriptorService idProviderDescriptorService = Mockito.mock( IdProviderDescriptorService.class );
        when( idProviderDescriptorService.getDescriptor( ApplicationKey.from( "myapplication" ) ) ).thenReturn(
            IdProviderDescriptor.create().key( ApplicationKey.from( "myapplication" ) ).build() );

        //Mocks the SecurityService
        final SecurityService securityService = Mockito.mock( SecurityService.class );
        final IdProvider emptyIdProvider = IdProvider.create().build();
        final IdProviderConfig idProviderConfig =
            IdProviderConfig.create().applicationKey( ApplicationKey.from( "myapplication" ) ).build();
        final IdProvider idProvider = IdProvider.create().idProviderConfig( idProviderConfig ).build();

        when( securityService.getIdProvider( IdProviderKey.from( "myemptyidprovider" ) ) ).thenReturn( emptyIdProvider );
        when( securityService.getIdProvider( IdProviderKey.from( "myidprovider" ) ) ).thenReturn( idProvider );

        when( securityService.getIdProvider( IdProviderKey.from( "myemptyuserstore" ) ) ).thenReturn( emptyIdProvider );
        when( securityService.getIdProvider( IdProviderKey.from( "myuserstore" ) ) ).thenReturn( idProvider );

        //Mocks the PortalScriptService
        final PortalScriptService portalScriptService = setupPortalScriptService();

        //Creates IdProviderControllerScriptFactoryImpl
        final IdProviderControllerScriptFactoryImpl idProviderControllerScriptFactory =
            new IdProviderControllerScriptFactoryImpl( portalScriptService );

        //Creates IdProviderControllerServiceImpl
        idProviderControllerService = new IdProviderControllerServiceImpl();
        idProviderControllerService.setIdProviderControllerScriptFactory( idProviderControllerScriptFactory );
        idProviderControllerService.setIdProviderDescriptorService( idProviderDescriptorService );
        idProviderControllerService.setSecurityService( securityService );
        idProviderControllerService.setResponseSerializationService( new WebSerializerServiceImpl() );
    }

    private PortalScriptService setupPortalScriptService()
    {
        final Application application = Mockito.mock( Application.class );
        when( application.getKey() ).thenReturn( ApplicationKey.from( "myapplication" ) );
        when( application.getVersion() ).thenReturn( Version.emptyVersion );
        when( application.getClassLoader() ).thenReturn( getClass().getClassLoader() );
        when( application.isStarted() ).thenReturn( true );
        when( application.getConfig() ).thenReturn( ConfigBuilder.create().build() );

        final Application defaultapplication = Mockito.mock( Application.class );
        when( defaultapplication.getKey() ).thenReturn( ApplicationKey.from( "defaultapplication" ) );
        when( defaultapplication.getVersion() ).thenReturn( Version.emptyVersion );
        when( defaultapplication.getClassLoader() ).thenReturn( getClass().getClassLoader() );
        when( defaultapplication.isStarted() ).thenReturn( true );
        when( defaultapplication.getConfig() ).thenReturn( ConfigBuilder.create().build() );

        ResourceService resourceService = Mockito.mock( ResourceService.class );
        when( resourceService.getResource( Mockito.any() ) ).thenAnswer( invocation -> {
            final ResourceKey resourceKey = invocation.getArgument( 0 );
            final URL resourceUrl =
                AbstractControllerTest.class.getResource( "/" + resourceKey.getApplicationKey() + resourceKey.getPath() );
            return new UrlResource( resourceKey, resourceUrl );
        } );

        final ScriptRuntimeFactory runtimeFactory =
            ScriptFixturesFacade.getInstance().scriptRuntimeFactory( resourceService, null, defaultapplication, application );

        final PortalScriptServiceImpl scriptService = new PortalScriptServiceImpl( runtimeFactory );
        scriptService.initialize();

        return scriptService;
    }

    @Test
    void executeMissingIdProvider()
        throws IOException
    {
        final IdProviderControllerExecutionParams executionParams = IdProviderControllerExecutionParams.create()
            .portalRequest( new PortalRequest() )
            .idProviderKey( IdProviderKey.from( "missingidprovider" ) )
            .functionName( "missingfunction" )
            .build();
        final PortalResponse portalResponse = idProviderControllerService.execute( executionParams );
        assertNull( portalResponse );
    }

    @Test
    void executeMissingFunction()
        throws IOException
    {
        final IdProviderControllerExecutionParams executionParams = IdProviderControllerExecutionParams.create()
            .portalRequest( new PortalRequest() )
            .idProviderKey( IdProviderKey.from( "myemptyidprovider" ) )
            .functionName( "missingfunction" )
            .build();
        final PortalResponse portalResponse = idProviderControllerService.execute( executionParams );
        assertNull( portalResponse );
    }

    @Test
    void executeIdProviderWithoutApplication()
        throws IOException
    {
        final IdProviderControllerExecutionParams executionParams = IdProviderControllerExecutionParams.create()
            .portalRequest( new PortalRequest() )
            .idProviderKey( IdProviderKey.from( "myemptyidprovider" ) )
            .functionName( "myfunction" )
            .build();
        final PortalResponse portalResponse = idProviderControllerService.execute( executionParams );
        assertNull( portalResponse );
    }

    @Test
    void execute()
        throws IOException
    {
        final IdProviderControllerExecutionParams executionParams = IdProviderControllerExecutionParams.create()
            .portalRequest( new PortalRequest() )
            .idProviderKey( IdProviderKey.from( "myidprovider" ) )
            .functionName( "myfunction" )
            .build();
        final PortalResponse portalResponse = idProviderControllerService.execute( executionParams );
        assertNotNull( portalResponse );
        assertEquals( HttpStatus.OK, portalResponse.getStatus() );
        assertEquals( "myapplication/myfunction", portalResponse.getBody() );
    }


    @Test
    void executeWithoutVirtualHost()
        throws IOException
    {
        final HttpServletRequest httpServletRequest = createHttpServletRequest();
        final IdProviderControllerExecutionParams executionParams =
            IdProviderControllerExecutionParams.create().servletRequest( httpServletRequest ).functionName( "myfunction" ).build();
        final PortalResponse portalResponse = idProviderControllerService.execute( executionParams );
        assertNull( portalResponse );
    }


    @Test
    void executeWithVirtualHost()
        throws IOException
    {
        final HttpServletRequest httpServletRequest = createHttpServletRequest();

        final VirtualHost virtualHost = Mockito.mock( VirtualHost.class );
        when( virtualHost.getDefaultIdProviderKey() ).thenReturn( IdProviderKey.from( "myuserstore" ) );
        when( virtualHost.getTarget() ).thenReturn( "/" );
        when( httpServletRequest.getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        VirtualHostHelper.setVirtualHost( httpServletRequest, virtualHost );

        final HttpServletResponse response = mock();
        when( response.isCommitted() ).thenReturn( true );
        final IdProviderControllerExecutionParams executionParams = IdProviderControllerExecutionParams.create()
            .servletRequest( httpServletRequest )
            .response( response )
            .functionName( "myfunction" )
            .build();
        final PortalResponse portalResponse = idProviderControllerService.execute( executionParams );
        assertNotNull( portalResponse );
        assertEquals( HttpStatus.OK, portalResponse.getStatus() );
        assertEquals( "myapplication/myfunction", portalResponse.getBody() );
    }

    private HttpServletRequest createHttpServletRequest()
    {
        final HttpServletRequest httpServletRequest = Mockito.mock( HttpServletRequest.class );
        when( httpServletRequest.getMethod() ).thenReturn( "GET" );
        when( httpServletRequest.getLocales() ).thenReturn( Collections.enumeration( Collections.singleton( Locale.US ) ) );
        when( httpServletRequest.getScheme() ).thenReturn( "http" );
        when( httpServletRequest.getServerName() ).thenReturn( "localhost" );
        when( httpServletRequest.getLocalPort() ).thenReturn( 80 );
        when( httpServletRequest.getRequestURI() ).thenReturn( "/admin" );
        when( httpServletRequest.getPathInfo() ).thenReturn( "/admin" );
        when( httpServletRequest.getHeaderNames() ).thenReturn( Collections.emptyEnumeration() );
        return httpServletRequest;
    }

    @Nested
    class ResolveFunctionName
    {
        private static final ApplicationKey APP_KEY = ApplicationKey.from( "myapp" );

        private static final IdProviderKey ID_PROVIDER_KEY = IdProviderKey.from( "myidprovider" );

        private IdProviderControllerServiceImpl service;

        private IdProviderControllerScript script;

        @BeforeEach
        void setup()
        {
            script = mock( IdProviderControllerScript.class );

            final IdProviderControllerScriptFactory scriptFactory = mock( IdProviderControllerScriptFactory.class );
            when( scriptFactory.fromScript( any() ) ).thenReturn( script );

            final IdProviderDescriptorService descriptorService = mock( IdProviderDescriptorService.class );
            when( descriptorService.getDescriptor( APP_KEY ) ).thenReturn(
                IdProviderDescriptor.create().key( APP_KEY ).build() );

            final IdProviderConfig idProviderConfig = IdProviderConfig.create().applicationKey( APP_KEY ).build();
            final IdProvider idProvider = IdProvider.create().idProviderConfig( idProviderConfig ).build();

            final SecurityService securityService = mock( SecurityService.class );
            when( securityService.getIdProvider( ID_PROVIDER_KEY ) ).thenReturn( idProvider );

            service = new IdProviderControllerServiceImpl();
            service.setIdProviderControllerScriptFactory( scriptFactory );
            service.setIdProviderDescriptorService( descriptorService );
            service.setSecurityService( securityService );
            service.setResponseSerializationService( new WebSerializerServiceImpl() );
        }

        @Test
        void exact_name_found()
            throws IOException
        {
            when( script.hasMethod( "login" ) ).thenReturn( true );
            when( script.execute( eq( "login" ), any() ) ).thenReturn( PortalResponse.create().status( HttpStatus.OK ).build() );

            final PortalResponse response = execute( "login" );
            assertNotNull( response );
            assertEquals( HttpStatus.OK, response.getStatus() );
        }

        @Test
        void exact_name_not_found()
            throws IOException
        {
            when( script.hasMethod( "login" ) ).thenReturn( false );

            assertNull( execute( "login" ) );
        }

        @Test
        void no_exact_name_resolves_uppercase_method()
            throws IOException
        {
            when( script.hasMethod( "GET" ) ).thenReturn( true );
            when( script.execute( eq( "GET" ), any() ) ).thenReturn( PortalResponse.create().status( HttpStatus.OK ).build() );

            final PortalResponse response = executeWithMethod( HttpMethod.GET );
            assertNotNull( response );
            assertEquals( HttpStatus.OK, response.getStatus() );
        }

        @Test
        void no_exact_name_resolves_lowercase_method()
            throws IOException
        {
            when( script.hasMethod( "POST" ) ).thenReturn( false );
            when( script.hasMethod( "post" ) ).thenReturn( true );
            when( script.execute( eq( "post" ), any() ) ).thenReturn( PortalResponse.create().status( HttpStatus.OK ).build() );

            final PortalResponse response = executeWithMethod( HttpMethod.POST );
            assertNotNull( response );
            assertEquals( HttpStatus.OK, response.getStatus() );
        }

        @Test
        void no_exact_name_no_method_match()
            throws IOException
        {
            when( script.hasMethod( "GET" ) ).thenReturn( false );
            when( script.hasMethod( "get" ) ).thenReturn( false );

            assertNull( executeWithMethod( HttpMethod.GET ) );
        }

        private PortalResponse execute( final String functionName )
            throws IOException
        {
            final IdProviderControllerExecutionParams params = IdProviderControllerExecutionParams.create()
                .portalRequest( new PortalRequest() )
                .idProviderKey( ID_PROVIDER_KEY )
                .functionName( functionName )
                .build();
            return service.execute( params );
        }

        private PortalResponse executeWithMethod( final HttpMethod method )
            throws IOException
        {
            final PortalRequest request = new PortalRequest();
            request.setMethod( method );
            final IdProviderControllerExecutionParams params = IdProviderControllerExecutionParams.create()
                .portalRequest( request )
                .idProviderKey( ID_PROVIDER_KEY )
                .build();
            return service.execute( params );
        }
    }
}
