package com.enonic.xp.testing.script;

import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
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
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.resource.Resource;
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
    private ApplicationKey applicationKey;

    protected PortalRequest portalRequest;

    protected ScriptSettings.Builder scriptSettings;

    private BundleContext bundleContext;

    public ScriptTestSupport()
    {
        setApplicationKey( "myapplication" );
    }

    @Before
    public final void setup()
    {
        setupRequest();

        this.scriptSettings = ScriptSettings.create();
        this.scriptSettings.basePath( "/site" );
        this.scriptSettings.binding( Context.class, ContextAccessor::current );
        this.scriptSettings.binding( PortalRequest.class, () -> this.portalRequest );

        this.bundleContext = Mockito.mock( BundleContext.class );
    }

    protected final void setApplicationKey( final String name )
    {
        this.applicationKey = ApplicationKey.from( name );
    }

    protected final ScriptExports runScript( final String path )
    {
        final ResourceKey key = ResourceKey.from( this.applicationKey, path );
        return runScript( key );
    }

    private ScriptExports runScript( final ResourceKey key )
    {
        return createRuntime().execute( key );
    }

    protected final ScriptValue runFunction( final String path, final String funcName )
    {
        final ScriptExports exports = runScript( path );

        Assert.assertNotNull( "No exports in [" + path + "]", exports );
        Assert.assertTrue( "No functions exported named [" + funcName + "] in [" + path + "]", exports.hasMethod( funcName ) );
        return exports.executeMethod( funcName );
    }

    private ScriptRuntime createRuntime()
    {
        final ScriptRuntimeFactoryImpl runtimeFactory = new ScriptRuntimeFactoryImpl();
        runtimeFactory.setApplicationService( createApplicationService() );
        runtimeFactory.setResourceService( createResourceService() );

        return runtimeFactory.create( this.scriptSettings.build() );
    }

    private ApplicationService createApplicationService()
    {
        final Application application = createApplication();

        final ApplicationService applicationService = Mockito.mock( ApplicationService.class );
        Mockito.when( applicationService.getApplication( this.applicationKey ) ).thenReturn( application );
        Mockito.when( applicationService.getClassLoader( Mockito.any() ) ).thenReturn( getClass().getClassLoader() );
        return applicationService;
    }

    private Application createApplication()
    {
        final Bundle bundle = createBundle();
        final Application application = Mockito.mock( Application.class );
        Mockito.when( application.getBundle() ).thenReturn( bundle );
        Mockito.when( application.getKey() ).thenReturn( this.applicationKey );
        Mockito.when( application.getVersion() ).thenReturn( Version.parseVersion( "1.0.0" ) );
        return application;
    }

    private Bundle createBundle()
    {
        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getBundleContext() ).thenReturn( this.bundleContext );
        return bundle;
    }

    private ResourceService createResourceService()
    {
        final ResourceService resourceService = Mockito.mock( ResourceService.class );
        Mockito.when( resourceService.getResource( Mockito.any() ) ).thenAnswer( this::loadResource );

        addService( ResourceService.class, resourceService );
        return resourceService;
    }

    private Resource loadResource( final InvocationOnMock invocation )
    {
        return loadResource( (ResourceKey) invocation.getArguments()[0] );
    }

    protected final Resource loadResource( final String path )
    {
        return loadResource( ResourceKey.from( this.applicationKey, path ) );
    }

    private Resource loadResource( final ResourceKey key )
    {
        final URL url = findResource( key.getPath() );
        return new UrlResource( key, url );
    }

    private URL findResource( final String path )
    {
        return getClass().getResource( path );
    }

    @SuppressWarnings("unchecked")
    protected final <T> void addService( final Class<T> type, final T instance )
    {
        final ServiceReference<T> ref = Mockito.mock( ServiceReference.class );
        Mockito.when( this.bundleContext.getServiceReference( type ) ).thenReturn( ref );
        Mockito.when( this.bundleContext.getService( ref ) ).thenReturn( instance );
    }

    private void setupRequest()
    {
        this.portalRequest = new PortalRequest();

        this.portalRequest.setMode( RenderMode.LIVE );
        this.portalRequest.setBranch( Branch.from( "draft" ) );
        this.portalRequest.setApplicationKey( ApplicationKey.from( "myapplication" ) );
        this.portalRequest.setBaseUri( "/portal" );

        final Content content = Content.create().id( ContentId.from( "123" ) ).path( "some/path" ).build();
        this.portalRequest.setContent( content );

        PortalRequestAccessor.set( this.portalRequest );
    }
}
