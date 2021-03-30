package com.enonic.xp.script.graaljs.impl;

import java.net.URL;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.graaljs.impl.executor.ScriptExecutor;
import com.enonic.xp.script.graaljs.impl.executor.ScriptExecutorImpl;
import com.enonic.xp.script.graaljs.impl.service.ServiceRegistryImpl;
import com.enonic.xp.script.runtime.ScriptSettings;
import com.enonic.xp.server.RunMode;

public class GraalJSTest
{
    @Test
    public void test() {
        final ApplicationKey APPLICATION_KEY = ApplicationKey.from( "myapplication" );

        final BundleContext bundleContext = Mockito.mock( BundleContext.class );

        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getBundleContext() ).thenReturn( bundleContext );

        final Application application = Mockito.mock( Application.class );
        Mockito.when( application.getBundle() ).thenReturn( bundle );
        Mockito.when( application.getKey() ).thenReturn( APPLICATION_KEY );
        Mockito.when( application.getVersion() ).thenReturn( Version.parseVersion( "1.0.0" ) );
        Mockito.when( application.getClassLoader() ).thenReturn( getClass().getClassLoader() );
        Mockito.when( application.isStarted() ).thenReturn( true );
        Mockito.when( application.getConfig() ).thenReturn( ConfigBuilder.create().build() );

        final ApplicationService applicationService = Mockito.mock( ApplicationService.class );
        Mockito.when( applicationService.getInstalledApplication( APPLICATION_KEY ) ).thenReturn( application );

        final ResourceService resourceService = Mockito.mock( ResourceService.class );
        Mockito.when( resourceService.getResource( Mockito.any() ) ).thenAnswer( invocation -> {
            final ResourceKey resourceKey = (ResourceKey) invocation.getArguments()[0];
            final URL resourceUrl = GraalJSDiscoveryTest.class.getResource( "/" + resourceKey.getApplicationKey() + resourceKey.getPath() );
            return new UrlResource( resourceKey, resourceUrl );
        } );

        final ScriptSettings scriptSettings = ScriptSettings.create().
            globalVariable( "xxx", "1243" ).build();

        final GraalJSContextProviderImpl contextProvider = new GraalJSContextProviderImpl();

        ScriptExecutor scriptExecutor =
            new ScriptExecutorImpl( contextProvider.getContext(), Executors.newSingleThreadExecutor(), scriptSettings,
                                    new ServiceRegistryImpl( bundleContext ), resourceService, application, RunMode.DEV );
        ScriptExports scriptExports = scriptExecutor.executeMain( ResourceKey.from( "myapplication:require-test.js" ) );

        scriptExports.executeMethod( "get" );

        contextProvider.deactivate();
    }
}
