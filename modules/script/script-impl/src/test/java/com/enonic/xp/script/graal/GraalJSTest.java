package com.enonic.xp.script.graal;

import java.io.Closeable;
import java.net.URL;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.graal.executor.GraalScriptExecutor;
import com.enonic.xp.script.impl.executor.ScriptExecutor;
import com.enonic.xp.script.impl.function.ApplicationInfoBuilder;
import com.enonic.xp.script.impl.service.ServiceRegistryImpl;
import com.enonic.xp.script.runtime.ScriptSettings;
import com.enonic.xp.server.RunMode;
import com.enonic.xp.util.Version;

class GraalJSTest
{
    @Test
    void test()
        throws Exception
    {
        final ApplicationKey APPLICATION_KEY = ApplicationKey.from( "graaljs" );

        final BundleContext bundleContext = Mockito.mock( BundleContext.class );

        final ApplicationInfoBuilder application =
            new ApplicationInfoBuilder( APPLICATION_KEY, ConfigBuilder.create().build(), Version.emptyVersion );

        final ResourceService resourceService = Mockito.mock( ResourceService.class );
        Mockito.when( resourceService.getResource( Mockito.any() ) ).thenAnswer( invocation -> {
            final ResourceKey resourceKey = (ResourceKey) invocation.getArguments()[0];
            final URL resourceUrl = GraalJSDiscoveryTest.class.getResource( "/" + resourceKey.getApplicationKey() + resourceKey.getPath() );
            return new UrlResource( resourceKey, resourceUrl );
        } );

        final ScriptSettings scriptSettings = ScriptSettings.create().globalVariable( "xxx", "1243" ).build();

        ScriptExecutor scriptExecutor =
            new GraalScriptExecutor( new GraalJSContextFactory(), Executors.newSingleThreadExecutor(), getClass().getClassLoader(),
                                     scriptSettings, new ServiceRegistryImpl( bundleContext ), resourceService, application, RunMode.DEV );

        ScriptExports scriptExports = scriptExecutor.executeMain( ResourceKey.from( "graaljs:require-test.js" ) );

        scriptExports.executeMethod( "get" );

        ( (Closeable) scriptExecutor ).close();
    }
}
