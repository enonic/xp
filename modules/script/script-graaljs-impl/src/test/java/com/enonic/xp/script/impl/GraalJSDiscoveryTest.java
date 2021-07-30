package com.enonic.xp.script.impl;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.function.Function;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;

import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.impl.executor.ScriptExecutor;
import com.enonic.xp.script.impl.executor.ScriptExecutorImpl;
import com.enonic.xp.script.impl.service.ServiceRegistryImpl;
import com.enonic.xp.script.runtime.ScriptSettings;
import com.enonic.xp.server.RunMode;

public class GraalJSDiscoveryTest
{
    @Test
    public void test()
        throws ScriptException
    {
        ScriptEngine nashornEngine = new ScriptEngineManager().getEngineByName( "nashorn" );
        nashornEngine.eval( "print('Hello World!');" );

        ScriptEngine graalEngine = new ScriptEngineManager().getEngineByName( "graal.js" );
        graalEngine.eval( "print('Hello World! GraalJS');" );

        try (Context context = Context.newBuilder().
            option( "js.ecmascript-version", "6" ).
            build())
        {
            Value jsBindings = context.getBindings( "js" );

            jsBindings.putMember( "myprop", "myvalue" );

            context.eval( "js", "print(myprop)" );
        }

        try (Context context = Context.create())
        {
            Value value = context.eval( "js", "(function(param){console.log('Hello, ' + param + '!');})" );
            value.execute( "Tolya" );
        }
    }

    @Test
    public void test2()
        throws ScriptException
    {
        ScriptEngine engine = GraalJSScriptEngine.create( new GraalJsEngineProviderImpl().getEngine(), Context.newBuilder( "js" ).
            allowExperimentalOptions( true ).
            allowHostAccess( HostAccess.ALL ).
            allowHostClassLookup( s -> true ).
            option( "js.ecmascript-version", "2021" ).
            option( "js.nashorn-compat", "true" ).
            option( "js.debug-builtin", "true" ) );
        engine.put( "javaObj", new Object() );
        engine.eval( "print(javaObj instanceof Java.type('java.lang.Object'));" );
    }

    @Test
    public void testFunction()
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
        try (Context context = Context.newBuilder( "js" ).
            allowExperimentalOptions( true ).
            allowHostClassLookup( s -> true ).
            option( "js.nashorn-compat", "true" ).
            build())
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
    public void test4()
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
    public void test5()
    {
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

        ScriptExecutor scriptExecutor =
            new ScriptExecutorImpl( new GraalJsEngineProviderImpl().getEngine(), Executors.newSingleThreadExecutor(), scriptSettings,
                                    new ServiceRegistryImpl( bundleContext ), resourceService, application, RunMode.DEV );

        ScriptExports scriptExports = scriptExecutor.executeMain( ResourceKey.from( "myapplication:require-test.js" ) );

        scriptExports.executeMethod( "get" );

        scriptExecutor.close();
    }

    public static abstract class AbstractFunction
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

    @Test
    @SuppressWarnings("unchecked")
    public void test6()
        throws Exception
    {
        GraalJSScriptEngine scriptEngine = GraalJSScriptEngine.create( null, Context.newBuilder( "js" ) );

        final ProxyObject exports = ProxyObject.fromMap( new HashMap<>() );

        SimpleBindings bindings = new SimpleBindings();
        bindings.put( "exports", exports );

        Function<Object[], Object> function = (Function<Object[], Object>) scriptEngine.eval(
            "(function (exports, name) { console.log('Name: ' + name); exports.get = function() { console.log('GET function'); }; })",
            bindings );

        System.out.println( function.apply( new Object[]{exports, "Anatol"} ) );

        final Value exportGetValue = (Value) exports.getMember( "get" );
        if ( exportGetValue.canExecute() )
        {
            exportGetValue.execute();
        }
    }

}
