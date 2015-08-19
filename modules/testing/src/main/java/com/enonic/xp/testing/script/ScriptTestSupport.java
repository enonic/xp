package com.enonic.xp.testing.script;

import java.net.URL;

import org.junit.Assert;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.impl.ScriptRuntimeFactoryImpl;
import com.enonic.xp.script.runtime.ScriptRuntime;
import com.enonic.xp.script.runtime.ScriptSettings;

public abstract class ScriptTestSupport
{
    private final static ApplicationKey DEFAULT_APPLICATION_KEY = ApplicationKey.from( "myapplication" );

    protected final ScriptRuntime scriptRuntime;

    private final BundleContext bundleContext;

    protected final PortalRequest portalRequest;

    protected final ResourceService resourceService;

    public ScriptTestSupport()
    {
        this.bundleContext = Mockito.mock( BundleContext.class );

        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getBundleContext() ).thenReturn( this.bundleContext );

        final Application application = Mockito.mock( Application.class );
        Mockito.when( application.getBundle() ).thenReturn( bundle );
        Mockito.when( application.getKey() ).thenReturn( DEFAULT_APPLICATION_KEY );
        Mockito.when( application.getVersion() ).thenReturn( Version.parseVersion( "1.0.0" ) );

        final ApplicationService applicationService = Mockito.mock( ApplicationService.class );
        Mockito.when( applicationService.getApplication( getApplicationKey() ) ).thenReturn( application );
        Mockito.when( applicationService.getClassLoader( Mockito.any() ) ).thenReturn( getClass().getClassLoader() );

        resourceService = Mockito.mock( ResourceService.class );
        Mockito.when( resourceService.getResource( Mockito.any() ) ).thenAnswer( invocation -> {
            final ResourceKey resourceKey = (ResourceKey) invocation.getArguments()[0];
            final URL resourceUrl = ScriptTestSupport.class.getResource( resourceKey.getPath() );
            return new UrlResource( resourceKey, resourceUrl );
        } );

        ServiceReference<ResourceService> resourceServiceReference = Mockito.mock( ServiceReference.class );
        Mockito.when( this.bundleContext.getServiceReference( ResourceService.class ) ).thenReturn( resourceServiceReference );
        Mockito.when( this.bundleContext.getService( resourceServiceReference ) ).thenReturn( resourceService );

        this.portalRequest = new PortalRequest();

        final ScriptRuntimeFactoryImpl scriptRuntimeFactory = new ScriptRuntimeFactoryImpl();
        scriptRuntimeFactory.setApplicationService( applicationService );
        scriptRuntimeFactory.setResourceService( resourceService );

        this.scriptRuntime = scriptRuntimeFactory.create( ScriptSettings.create().build() );
    }

    protected final void setupRequest()
    {
        this.portalRequest.setMode( RenderMode.LIVE );
        this.portalRequest.setBranch( Branch.from( "draft" ) );
        this.portalRequest.setApplicationKey( ApplicationKey.from( "myapplication" ) );
        this.portalRequest.setBaseUri( "/portal" );

        final Content content = Content.create().id( ContentId.from( "123" ) ).path( "some/path" ).build();
        this.portalRequest.setContent( content );

        PortalRequestAccessor.set( this.portalRequest );
    }

    @SuppressWarnings("unchecked")
    protected final <T> void addService( final Class<T> type, final T instance )
    {
        final ServiceReference<T> ref = Mockito.mock( ServiceReference.class );
        Mockito.when( this.bundleContext.getServiceReference( type ) ).thenReturn( ref );
        Mockito.when( this.bundleContext.getService( ref ) ).thenReturn( instance );
    }

    protected final ScriptExports runTestScript( final String path )
    {
        return runTestScript( ResourceKey.from( getApplicationKey(), "/site/" + path ) );
    }

    private ScriptExports runTestScript( final ResourceKey key )
    {
        return this.scriptRuntime.execute( key );
    }

    protected final ScriptValue runTestFunction( final String path, final String funcName )
        throws Exception
    {
        final ScriptExports exports = runTestScript( path );

        Assert.assertNotNull( "No exports in [" + path + "]", exports );
        Assert.assertTrue( "No functions exported named [" + funcName + "] in [" + path + "]", exports.hasMethod( funcName ) );
        return exports.executeMethod( funcName );
    }

    protected ApplicationKey getApplicationKey()
    {
        return DEFAULT_APPLICATION_KEY;
    }
}
