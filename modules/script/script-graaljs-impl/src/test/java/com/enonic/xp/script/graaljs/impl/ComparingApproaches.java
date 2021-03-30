package com.enonic.xp.script.graaljs.impl;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyArray;
import org.graalvm.polyglot.proxy.ProxyExecutable;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;

import com.oracle.truffle.js.scriptengine.GraalJSEngineFactory;
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.graaljs.impl.executor.ScriptEngineExecutorImpl;
import com.enonic.xp.script.graaljs.impl.executor.ScriptExecutor;
import com.enonic.xp.script.runtime.ScriptSettings;
import com.enonic.xp.server.RunMode;

public class ComparingApproaches
{
    @Test
    public void testScriptEngine()
    {
        ApplicationKey APPLICATION_KEY = ApplicationKey.from( "myapp" );

        BundleContext bundleContext = Mockito.mock( BundleContext.class );

        Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getBundleContext() ).thenReturn( bundleContext );

        Application application = Mockito.mock( Application.class );
        Mockito.when( application.getBundle() ).thenReturn( bundle );
        Mockito.when( application.getKey() ).thenReturn( APPLICATION_KEY );
        Mockito.when( application.getVersion() ).thenReturn( Version.parseVersion( "1.0.0" ) );
        Mockito.when( application.getClassLoader() ).thenReturn( getClass().getClassLoader() );
        Mockito.when( application.isStarted() ).thenReturn( true );
        Mockito.when( application.getConfig() ).thenReturn( ConfigBuilder.create().build() );

        ApplicationService applicationService = Mockito.mock( ApplicationService.class );
        Mockito.when( applicationService.getInstalledApplication( APPLICATION_KEY ) ).thenReturn( application );

        ResourceService resourceService = Mockito.mock( ResourceService.class );
        Mockito.when( resourceService.getResource( Mockito.any() ) ).thenAnswer( invocation -> {
            ResourceKey resourceKey = (ResourceKey) invocation.getArguments()[0];
            URL resourceUrl = ComparingApproaches.class.getResource( "/" + resourceKey.getApplicationKey() + resourceKey.getPath() );
            return new UrlResource( resourceKey, resourceUrl );
        } );

        ScriptSettings scriptSettings = ScriptSettings.create().build();

        GraalJSContextProviderImpl contextProvider = new GraalJSContextProviderImpl();

        ScriptExecutor scriptExecutor = new ScriptEngineExecutorImpl( scriptSettings, resourceService, application, RunMode.DEV );
        ScriptExports scriptExports = scriptExecutor.executeMain( ResourceKey.from( "myapp:script.js" ) );

        scriptExports.executeMethod( "execute" );

        contextProvider.deactivate();
    }

    @Test
    public void testProxy()
    {
        try (Context context = Context.newBuilder( "js" ).allowHostAccess( HostAccess.ALL ).allowHostClassLookup( clz -> true ).build())
        {
            Map<String, Object> proxyAsMap = new HashMap<>();
            proxyAsMap.put( "execute", (ProxyExecutable) arguments -> {
                System.out.println( "To do something" );
                return null;
            } );
            context.getBindings( "js" ).putMember( "bean", ProxyObject.fromMap( proxyAsMap ) );
            context.eval( "js", "bean.execute()" );
        }
    }

    @Test
    public void testEngine() throws Exception
    {
//        GraalJSEngineFactory factory =  new GraalJSEngineFactory();
//        ScriptEngine scriptEngine = factory.getScriptEngine();
        ScriptEngine engine = GraalJSScriptEngine.create( null,
                                                          Context.newBuilder("js")
                                                             .allowHostAccess(HostAccess.ALL)
                                                             .allowHostClassLookup(s -> true)
                                                          .allowExperimentalOptions( true ).
                                                              option( "js.nashorn-compat", "true" )
                                                             /*.option("js.ecmascript-version", "2021")*/);


        GraalScripBean bean  = new GraalScripBean();
        engine.put("javaObj", bean);
        System.out.println(engine.eval("javaObj.source = 'sdsds';"));
        System.out.println(engine.eval("javaObj.execute();"));

//        engine.setBindings(  );
    }

    @Test
    public void withExperimentalOptions()
    {
        MyBean javaObject = new MyBean();
        javaObject.setValue( "value" );

        InnerBean innerBean = new InnerBean();
        innerBean.setValue( "Value of InnerBean" );
        javaObject.setInnerBean( innerBean );

        try (Context context = Context.newBuilder( "js" ).
            allowHostAccess( HostAccess.ALL ).
            allowHostClassLookup( clz -> true ).
            allowExperimentalOptions( true ).
            option( "js.nashorn-compat", "true" ).build())
        {
            context.getBindings( "js" ).putMember( "javaObject", javaObject );

            System.out.println( context.eval( "js", "javaObject.getValue()" ) );
            context.eval( "js", "javaObject.setValue('123')" );
            System.out.println( context.eval( "js", "javaObject.getValue()" ) );
            context.eval( "js", "javaObject.value = '1234'" );
            System.out.println( context.eval( "js", "javaObject.value" ) );

            System.out.println( context.eval( "js", "javaObject.innerBean.value" ) );
            System.out.println( context.eval( "js", "javaObject.innerBean.value = '123456'" ) );
            System.out.println( context.eval( "js", "javaObject.innerBean.value" ) );
        }
    }

    @Test
    public void customProxyBean()
    {
        MyBean javaObject = new MyBean();
        javaObject.setValue( "value" );

        InnerBean innerBean = new InnerBean();
        innerBean.setValue( "Value of InnerBean" );
        javaObject.setInnerBean( innerBean );

        try (Context context = Context.newBuilder( "js" )
            .allowHostAccess( HostAccess.ALL )
            .allowHostClassLookup( clz -> true )
            .build())
        {
            context.getBindings( "js" ).putMember( "bean", BeanProxy.proxy( context.asValue( javaObject ) ) );
            System.out.println( context.eval( "js", "bean.value" ).asString() );
            context.eval( "js", "bean.value = '1234561'" );
            System.out.println( context.eval( "js", "bean.value" ).asString() );

            context.eval( "js", "bean.v = 'String value'" );
            System.out.println( context.eval( "js", "bean.v" ).asString() );
            System.out.println( context.eval( "js", "bean.getV()" ).asString() );

            Value valueInnerBean = Value.asValue( context.eval( "js", "bean.innerBean" ) );

            System.out.println( valueInnerBean.getMember( "toString()" ).execute(  ) );
            System.out.println( valueInnerBean.getMember( "getValue()" ).execute(  ) );
            System.out.println( Value.asValue( BeanProxy.proxy( valueInnerBean ) ).getMember( "value" ) );

            System.out.println( context.eval( "js", "bean.innerBean.getValue()" ).asString() );
            System.out.println( context.eval( "js", "bean.innerBean.value" ).asString() );
        }
    }

    public static class InnerBean
    {
        private String value;

        public String getValue()
        {
            return value;
        }

        public void setValue( String value )
        {
            this.value = value;
        }

        @Override
        public String toString()
        {
            return "InnerBean{" + "value='" + value + '\'' + '}';
        }
    }

    public static class MyBean
    {
        private String value;

        private String v;

        private InnerBean innerBean;

        public String getValue()
        {
            return value;
        }

        public void setValue( final String value )
        {
            this.value = value;
        }

        public String getV()
        {
            return v;
        }

        public void setV( final String v )
        {
            this.v = v;
        }

        public InnerBean getInnerBean()
        {
            return innerBean;
        }

        public void setInnerBean( final InnerBean innerBean )
        {
            this.innerBean = innerBean;
        }
    }

    private static class BeanProxy
        implements ProxyObject
    {
        private final Value value;

        BeanProxy( Value delegate )
        {
            this.value = delegate;
        }

        @Override
        public Object getMember( String key )
        {
            Value member = getGetter( key );
            if ( member != null )
            {
//                return member;
                return member.canExecute() ? member.execute() : BeanProxy.proxy( Value.asValue( member ) );
            }
            else
            {
                return value.getMember( key );
            }
        }

        @Override
        public Object getMemberKeys()
        {
            return new ProxyArray()
            {
                private final Object[] keys = value.getMemberKeys().toArray();

                @Override
                public void set( long index, Value value )
                {
                    throw new UnsupportedOperationException();
                }

                @Override
                public long getSize()
                {
                    return keys.length;
                }

                @Override
                public Object get( long index )
                {
                    if ( index < 0 || index > Integer.MAX_VALUE )
                    {
                        throw new ArrayIndexOutOfBoundsException();
                    }
                    return keys[(int) index];
                }
            };
        }

        @Override
        public boolean hasMember( String key )
        {
            return getGetter( key ) != null || value.hasMember( key );
        }

        @Override
        public void putMember( String key, Value value )
        {
            Value member = getSetter( key );
            if ( member != null && member.canExecute() )
            {
                member.execute( value );
            }
            else
            {
                this.value.putMember( key, value );
            }
        }

        private Value getGetter( String key )
        {
            Value getter = value.getMember( "get" + capitalize( key ) );
            if ( getter == null )
            {
                getter = value.getMember( "is" + capitalize( key ) );
            }
            return getter;
        }

        private Value getSetter( String key )
        {
            return value.getMember( "set" + capitalize( key ) );
        }

        private String capitalize( String value )
        {
            if ( value.isEmpty() )
            {
                return value;
            }
            return value.substring( 0, 1 ).toUpperCase() + value.substring( 1 );
        }

        public static Object proxy( Value bean )
        {
            if ( bean.isHostObject() && bean.asHostObject() instanceof BeanProxy )
            {
                return bean;
            }
            else
            {
                return new BeanProxy( bean );
            }
        }
    }
}
