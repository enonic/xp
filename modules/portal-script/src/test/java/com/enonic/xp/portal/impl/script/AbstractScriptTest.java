package com.enonic.xp.portal.impl.script;

import java.net.URL;

import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.portal.script.ScriptExports;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.ResourceUrlRegistry;
import com.enonic.xp.resource.ResourceUrlTestHelper;

public abstract class AbstractScriptTest
{
    private final static ApplicationKey APPLICATION_KEY = ApplicationKey.from( "mymodule" );

    protected final ScriptServiceImpl scriptService;

    public AbstractScriptTest()
    {
        this.scriptService = new ScriptServiceImpl();
        this.scriptService.addGlobalVariable( "assert", new AssertHelper() );

        final ResourceUrlRegistry urlRegistry = ResourceUrlTestHelper.mockApplicationScheme();
        urlRegistry.applicationsClassLoader( getClass().getClassLoader() );

        final BundleContext bundleContext = Mockito.mock( BundleContext.class );

        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getBundleContext() ).thenReturn( bundleContext );

        final Application application = Mockito.mock( Application.class );
        Mockito.when( application.getBundle() ).thenReturn( bundle );

        final ApplicationService applicationService = Mockito.mock( ApplicationService.class );
        Mockito.when( applicationService.getApplication( APPLICATION_KEY ) ).thenReturn( application );
        Mockito.when( applicationService.getClassLoader( Mockito.any() ) ).thenReturn( getClass().getClassLoader() );

        final ResourceService resourceService = Mockito.mock( ResourceService.class );
        Mockito.when( resourceService.getResource( Mockito.any() ) ).thenAnswer( invocation -> {
            final ResourceKey resourceKey = (ResourceKey) invocation.getArguments()[0];
            return new Resource( resourceKey, new URL( "module:" + resourceKey.toString() ) );
        } );

        this.scriptService.setApplicationService( applicationService );
        this.scriptService.setResourceService( resourceService );
    }

    protected final ScriptExports runTestScript( final String name )
    {
        return runTestScript( ResourceKey.from( APPLICATION_KEY, name ) );
    }

    protected final ScriptExports runTestScript( final ResourceKey key )
    {
        return this.scriptService.execute( key );
    }
}
