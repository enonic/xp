package com.enonic.xp.testing.script;

import java.util.Map;

import org.junit.Assert;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.impl.ScriptRuntimeFactoryImpl;
import com.enonic.xp.script.runtime.ScriptRuntime;
import com.enonic.xp.script.runtime.ScriptSettings;

public abstract class ScriptTestSupport
    extends AbstractScriptTest
{
    protected ScriptSettings.Builder scriptSettings;

    public ScriptTestSupport()
    {
        setApplicationKey( "myapplication" );
    }

    @Override
    protected void initialize()
    {
        super.initialize();
        this.scriptSettings = ScriptSettings.create();
        this.scriptSettings.basePath( "/site" );
        this.scriptSettings.binding( Context.class, ContextAccessor::current );
        this.scriptSettings.binding( PortalRequest.class, () -> this.portalRequest );
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
        runtimeFactory.setResourceService( this.resourceService );

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
        final BundleContext bundleContext = createBundleContext();

        Mockito.when( bundle.getBundleContext() ).thenReturn( bundleContext );
        return bundle;
    }

    private BundleContext createBundleContext()
    {
        final BundleContext bundleContext = Mockito.mock( BundleContext.class );
        for ( final Map.Entry<Class, Object> service : this.services.entrySet() )
        {
            registerService( bundleContext, service.getKey(), service.getValue() );
        }

        return bundleContext;
    }

    private void registerService( final BundleContext context, final Class type, final Object instance )
    {
        final ServiceReference ref = Mockito.mock( ServiceReference.class );
        Mockito.when( context.getServiceReference( type ) ).thenReturn( ref );
        Mockito.when( context.getService( ref ) ).thenReturn( instance );
    }
}
