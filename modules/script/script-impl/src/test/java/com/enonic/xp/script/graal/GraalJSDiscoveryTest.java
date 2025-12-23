package com.enonic.xp.script.graal;

import java.io.Closeable;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.junit.jupiter.api.Disabled;
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

class GraalJSDiscoveryTest
{
    @Test
    void testFunction()
        throws Exception
    {
        Context context = Context.create( "js" );

        final String source = Files.readString( Paths.get( getClass().getResource( "/graaljs/function-script.js" ).toURI() ) );

        Value function = context.eval( "js", source );

        if ( function.canExecute() )
        {
            Value executionResult = function.execute( "undefinedValue" );
            System.out.println( executionResult.isNull() ? " --- " : executionResult.asString() );
        }
    }

    @Test
    @Disabled
    public void testFunction2()
        throws Exception
    {
        try (Context context = Context.newBuilder( "js" ).allowHostClassLookup( s -> true ).build())
        {
            final String source = Files.readString( Paths.get( getClass().getResource( "/graaljs/java-script.js" ).toURI() ) );

            Value function = context.eval( "js", source );
            if ( function.canExecute() )
            {
                function.executeVoid();
            }
        }
    }

    @Test
    //https://github.com/oracle/graaljs/issues/206
    public void test3()
    {
        String jsCode = "for (var key in jsonObject) { \n " + "  print('setting: '+key); \n" + "  jsonObject[key] = {'a':'b'}; \n" + "}; ";

        Map<String, Object> jsonObject = new LinkedHashMap<>();
        jsonObject.put( "foo", "aaa" );
        ProxyObject initDataProxy = ProxyObject.fromMap( jsonObject );

        try (Context jsContext = Context.newBuilder( "js" ).allowHostAccess( HostAccess.EXPLICIT ).build())
        {
            jsContext.getBindings( "js" ).putMember( "jsonObject", initDataProxy );
            jsContext.eval( "js", jsCode );

            System.out.println( jsonObject.get( "foo" ) );
            System.out.println( jsonObject.get( "wrong" ) );
        }
    }

    @Test
    void test4()
    {
        Map<String, Object> beanAsMap = new HashMap<>();
        beanAsMap.put( "require", new RequireFunction() );

        ProxyObject bean = ProxyObject.fromMap( beanAsMap );

        try (Context context = Context.newBuilder( "js" ).build())
        {
            context.getBindings( "js" ).putMember( "bean", bean );

            context.eval( "js", "bean.require('/lib/xp/content')" );
        }
    }

    @Test
    void test5()
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

    public abstract static class AbstractFunction
        implements ProxyExecutable
    {
        private final String name;

        public AbstractFunction( final String name )
        {
            this.name = name;
        }

        public String getName()
        {
            return name;
        }
    }

    public static class RequireFunction
        extends AbstractFunction
    {
        public RequireFunction()
        {
            super( "require" );
        }

        @Override
        public Object execute( final Value... arguments )
        {
            if ( arguments.length != 1 )
            {
                throw new IllegalArgumentException( "require(..) must have one parameter" );
            }

            System.out.println( "Try to find resource with path " + arguments[0].asString() );

            return arguments[0].asString();
        }
    }
}
