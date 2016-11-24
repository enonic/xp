package com.enonic.xp.testing.script;

import java.net.URL;
import java.util.Hashtable;

import org.junit.Assert;
import org.junit.Before;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.impl.app.ApplicationBuilder;
import com.enonic.xp.core.impl.app.resolver.ClassLoaderApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resource.ResourceServiceImpl;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.view.ViewFunctionService;
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
    public final static ApplicationKey APP_KEY = ApplicationKey.from( "myapp" );

    protected PortalRequest portalRequest;

    protected ApplicationService applicationService;

    protected ResourceService resourceService;

    protected ScriptSettings.Builder scriptSettings;

    protected BundleContext bundleContext;

    @Before
    public final void setup()
        throws Exception
    {
        initialize();
    }

    protected void initialize()
        throws Exception
    {
        this.portalRequest = createPortalRequest();
        PortalRequestAccessor.set( this.portalRequest );

        this.bundleContext = createBundleContext();
        this.applicationService = createApplicationService();
        this.resourceService = createResourceService();

        addService( ResourceService.class, this.resourceService );

        final ViewFunctionService viewFunctionService = new ViewFunctionsMockFactory().newService();
        addService( ViewFunctionService.class, viewFunctionService );

        this.scriptSettings = ScriptSettings.create();
        this.scriptSettings.binding( Context.class, ContextAccessor::current );
        this.scriptSettings.binding( PortalRequest.class, () -> this.portalRequest );
        this.scriptSettings.debug( new ScriptDebugSettings() );
        this.scriptSettings.globalVariable( "testInstance", this );
    }

    protected final <T> void addService( final Class<T> type, final T instance )
    {
        registerService( type, instance );
    }

    private PortalRequest createPortalRequest()
    {
        final PortalRequest request = new PortalRequest();

        request.setMode( RenderMode.LIVE );
        request.setBranch( Branch.from( "draft" ) );
        request.setApplicationKey( ApplicationKey.from( "myapplication" ) );
        request.setBaseUri( "/portal" );

        final Content content = Content.create().id( ContentId.from( "123" ) ).path( "some/path" ).build();
        request.setContent( content );
        return request;
    }

    private ResourceService createResourceService()
    {
        final ResourceServiceImpl resourceService = new ResourceServiceImpl();
        resourceService.setApplicationService( this.applicationService );
        return resourceService;
    }

    protected final Resource loadResource( final ResourceKey key )
    {
        final URL url = findResource( key.getPath() );
        return new UrlResource( key, url );
    }

    private URL findResource( final String path )
    {
        return getClass().getResource( path );
    }

    public final ScriptExports runScript( final String path )
    {
        final ResourceKey key = ResourceKey.from( APP_KEY, path );
        return runScript( key );
    }

    public final ScriptExports runScript( final ResourceKey key )
    {
        return createRuntime().execute( key );
    }

    protected final ScriptValue runFunction( final String path, final String funcName, final Object... funcParams )
    {
        final ScriptExports exports = runScript( path );

        Assert.assertNotNull( "No exports in [" + path + "]", exports );
        Assert.assertTrue( "No functions exported named [" + funcName + "] in [" + path + "]", exports.hasMethod( funcName ) );
        return exports.executeMethod( funcName, funcParams );
    }

    private ScriptRuntime createRuntime()
    {
        final ScriptRuntimeFactoryImpl runtimeFactory = new ScriptRuntimeFactoryImpl();
        runtimeFactory.setApplicationService( this.applicationService );
        runtimeFactory.setResourceService( this.resourceService );

        return runtimeFactory.create( this.scriptSettings.build() );
    }

    private ApplicationService createApplicationService()
    {
        final Application application = createApplication();

        final ApplicationService applicationService = Mockito.mock( ApplicationService.class );
        Mockito.when( applicationService.getInstalledApplication( APP_KEY ) ).thenReturn( application );
        return applicationService;
    }

    private Application createApplication()
    {
        final ApplicationBuilder builder = new ApplicationBuilder();
        builder.classLoader( getClass().getClassLoader() );
        builder.urlResolver( new ClassLoaderApplicationUrlResolver( getClass().getClassLoader() ) );
        builder.config( ConfigBuilder.create().build() );
        builder.bundle( createBundle() );
        return builder.build();
    }

    private Bundle createBundle()
    {
        final Bundle bundle = Mockito.mock( Bundle.class );

        Mockito.when( bundle.getBundleContext() ).thenReturn( this.bundleContext );
        Mockito.when( bundle.getSymbolicName() ).thenReturn( APP_KEY.getName() );
        Mockito.when( bundle.getVersion() ).thenReturn( Version.valueOf( "1.0.0" ) );
        Mockito.when( bundle.getState() ).thenReturn( Bundle.ACTIVE );

        final Hashtable<String, String> headers = new Hashtable<>();
        Mockito.when( bundle.getHeaders() ).thenReturn( headers );

        return bundle;
    }

    private BundleContext createBundleContext()
    {
        return Mockito.mock( BundleContext.class );
    }

    private void registerService( final Class type, final Object instance )
    {
        final ServiceReference ref = Mockito.mock( ServiceReference.class );
        Mockito.when( this.bundleContext.getServiceReference( type ) ).thenReturn( ref );
        Mockito.when( this.bundleContext.getService( ref ) ).thenReturn( instance );
    }
}
