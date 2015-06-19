package com.enonic.xp.testing.script;

import org.junit.Assert;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.script.ScriptServiceImpl;
import com.enonic.xp.portal.script.ScriptExports;
import com.enonic.xp.portal.script.ScriptValue;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.testing.resource.ResourceUrlRegistry;
import com.enonic.xp.testing.resource.ResourceUrlTestHelper;

public abstract class ScriptTestSupport
{
    private final static ModuleKey DEFAULT_MODULE_KEY = ModuleKey.from( "mymodule" );

    protected final ScriptServiceImpl scriptService;

    private final BundleContext bundleContext;

    protected final PortalRequest portalRequest;

    public ScriptTestSupport()
    {
        this.scriptService = new ScriptServiceImpl();

        final ResourceUrlRegistry urlRegistry = ResourceUrlTestHelper.mockModuleScheme();
        urlRegistry.modulesClassLoader( getClass().getClassLoader() );

        this.bundleContext = Mockito.mock( BundleContext.class );

        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getBundleContext() ).thenReturn( this.bundleContext );

        final Module module = Mockito.mock( Module.class );
        Mockito.when( module.getClassLoader() ).thenReturn( getClass().getClassLoader() );
        Mockito.when( module.getBundle() ).thenReturn( bundle );

        final ModuleService moduleService = Mockito.mock( ModuleService.class );
        Mockito.when( moduleService.getModule( getModuleKey() ) ).thenReturn( module );

        this.scriptService.setModuleService( moduleService );
        this.portalRequest = new PortalRequest();
    }

    protected final void setupRequest()
    {
        this.portalRequest.setMode( RenderMode.LIVE );
        this.portalRequest.setBranch( Branch.from( "draft" ) );
        this.portalRequest.setModule( ModuleKey.from( "mymodule" ) );
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
        return runTestScript( ResourceKey.from( getModuleKey(), path ) );
    }

    private ScriptExports runTestScript( final ResourceKey key )
    {
        return this.scriptService.execute( key );
    }

    protected final ScriptValue runTestFunction( final String path, final String funcName )
        throws Exception
    {
        final ScriptExports exports = runTestScript( path );

        Assert.assertNotNull( "No exports in [" + path + "]", exports );
        Assert.assertTrue( "No functions exported named [" + funcName + "] in [" + path + "]", exports.hasMethod( funcName ) );
        return exports.executeMethod( funcName );
    }

    protected ModuleKey getModuleKey()
    {
        return DEFAULT_MODULE_KEY;
    }
}
