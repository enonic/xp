package com.enonic.xp.portal.impl.error;

import java.net.URL;
import java.util.Hashtable;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.postprocess.PostProcessorImpl;
import com.enonic.xp.portal.impl.script.PortalScriptServiceImpl;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.script.ScriptFixturesFacade;
import com.enonic.xp.script.impl.async.ScriptAsyncService;
import com.enonic.xp.script.runtime.ScriptRuntimeFactory;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.servlet.ServletRequestHolder;

import static org.mockito.Mockito.when;

public abstract class AbstractErrorHandlerTest
{
    protected PostProcessorImpl postProcessor;

    private ErrorHandlerScriptFactoryImpl factory;

    protected PortalRequest portalRequest;

    protected PortalResponse portalResponse;

    protected ResourceService resourceService;

    public AbstractErrorHandlerTest()
    {
    }

    @BeforeEach
    public void setup()
        throws Exception
    {
        this.portalRequest = new PortalRequest();
        this.portalRequest.setMethod( HttpMethod.GET );
        this.portalResponse = PortalResponse.create().build();

        final BundleContext bundleContext = Mockito.mock( BundleContext.class );

        final Bundle bundle = Mockito.mock( Bundle.class );
        when( bundle.getBundleContext() ).thenReturn( bundleContext );
        when( bundle.getHeaders() ).thenReturn( new Hashtable<>() );

        final Application application = Mockito.mock( Application.class );
        when( application.getBundle() ).thenReturn( bundle );
        when( application.getClassLoader() ).thenReturn( getClass().getClassLoader() );
        when( application.isStarted() ).thenReturn( true );
        when( application.getConfig() ).thenReturn( ConfigBuilder.create().build() );

        final ApplicationService applicationService = Mockito.mock( ApplicationService.class );
        when( applicationService.getInstalledApplication( ApplicationKey.from( "myapplication" ) ) ).thenReturn( application );

        this.resourceService = Mockito.mock( ResourceService.class );
        when( resourceService.getResource( Mockito.any() ) ).thenAnswer( invocation -> {
            final ResourceKey resourceKey = (ResourceKey) invocation.getArguments()[0];
            final URL resourceUrl =
                AbstractErrorHandlerTest.class.getResource( "/" + resourceKey.getApplicationKey() + resourceKey.getPath() );
            return new UrlResource( resourceKey, resourceUrl );
        } );

        final ScriptAsyncService scriptAsyncService = Mockito.mock( ScriptAsyncService.class );

        final ScriptRuntimeFactory runtimeFactory =
            ScriptFixturesFacade.getInstance().scriptRuntimeFactory( applicationService, resourceService, scriptAsyncService );

        final PortalScriptServiceImpl scriptService = new PortalScriptServiceImpl( runtimeFactory );
        scriptService.initialize();

        this.factory = new ErrorHandlerScriptFactoryImpl( scriptService );

        this.postProcessor = new PostProcessorImpl();

        final HttpServletRequest req = Mockito.mock( HttpServletRequest.class );
        ServletRequestHolder.setRequest( req );
    }

    protected final void execute( final String script, final HttpStatus status )
    {
        final ErrorHandlerScript controllerScript = this.factory.errorScript( ResourceKey.from( script ) );
        final PortalError portalError = PortalError.create().request( this.portalRequest ).status( status ).build();
        this.portalResponse = controllerScript.execute( portalError, "handle" + status );
        if ( this.portalResponse == null )
        {
            this.portalResponse = controllerScript.execute( portalError, "handleError" );
        }
    }

}
