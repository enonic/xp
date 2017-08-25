package com.enonic.xp.testkit;

import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.reflect.ClassPath;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.impl.app.ApplicationBuilder;
import com.enonic.xp.core.impl.app.resolver.ClassLoaderApplicationUrlResolver;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.impl.executor.ScriptExecutor;
import com.enonic.xp.script.impl.executor.ScriptExecutorImpl;
import com.enonic.xp.script.runtime.ScriptSettings;
import com.enonic.xp.server.RunMode;
import com.enonic.xp.testkit.mock.MockServiceRegistry;
import com.enonic.xp.testkit.resource.ClassLoaderResourceService;

public final class ScriptRunner
    extends Runner
{
    private static final ClassPath CP = initClassPath();

    private final Class<?> testClass;

    private final ScriptTestSuite testInstance;

    private final ApplicationKey appKey;

    public ScriptRunner( final Class<?> testClass )
        throws Exception
    {
        this.testClass = testClass;
        this.testInstance = (ScriptTestSuite) this.testClass.newInstance();
        this.appKey = this.testInstance.getAppKey();
    }

    @Override
    public Description getDescription()
    {
        return Description.createSuiteDescription( this.testClass );
    }

    @Override
    public void run( final RunNotifier notifier )
    {
        final List<ResourceKey> resources = findResources();
        for ( final ResourceKey resource : resources )
        {
            run( notifier, resource );
        }
    }

    private String composeName( final ResourceKey path )
    {
        return path.getPath().replaceAll( "(.+)\\.(.+)", "$1" );
    }

    private void run( final RunNotifier notifier, final ResourceKey path )
    {
        final ScriptTestInstance instance = run( notifier, path, "_main", null, () -> createScriptTestFile( path ) );
        if ( instance == null )
        {
            return;
        }

        for ( final ScriptTestMethod method : instance.getTestMethods() )
        {
            run( notifier, path, method.getName(), instance, () -> {
                method.runTest( testInstance );
                return null;
            } );
        }

        instance.dispose();
    }

    private <T> T run( final RunNotifier notifier, final ResourceKey path, final String name, final ScriptTestInstance file,
                       final Callable<T> exec )
    {
        final Description desc = Description.createTestDescription( composeName( path ), name );
        notifier.fireTestStarted( desc );

        try
        {
            runBefore( file );
            final T result = exec.call();

            runAfter( file );
            return result;
        }
        catch ( final Throwable e )
        {
            notifier.fireTestFailure( new Failure( desc, e ) );
            return null;
        }
        finally
        {
            notifier.fireTestFinished( desc );
        }
    }

    private void runBefore( final ScriptTestInstance file )
        throws Exception
    {
        this.testInstance.setUp();

        if ( file != null )
        {
            file.runBefore();
        }
    }

    private void runAfter( final ScriptTestInstance file )
        throws Exception
    {
        if ( file != null )
        {
            file.runAfter();
        }

        this.testInstance.tearDown();
    }

    private List<ResourceKey> findResources()
    {
        final List<Pattern> patterns = compileFilePatterns();
        final List<ResourceKey> list = Lists.newArrayList();

        for ( final ClassPath.ResourceInfo info : CP.getResources() )
        {
            matchesResource( info, patterns, list );
        }

        return list;
    }

    private List<Pattern> compileFilePatterns()
    {
        return getTestFiles().stream().map( this::compileFilePattern ).collect( Collectors.toList() );
    }

    private Pattern compileFilePattern( final String path )
    {
        final String pattern = path.
            replace( ".", "\\." ).
            replace( "*", "[^/]*" ).
            replace( "**", ".*" );

        return Pattern.compile( pattern );
    }

    private List<String> getTestFiles()
    {
        final List<String> files = Lists.newArrayList( this.testInstance.getTestFiles() );
        return files.stream().map( String::trim ).filter( ( str ) -> !Strings.isNullOrEmpty( str ) ).collect( Collectors.toList() );
    }

    private void matchesResource( final ClassPath.ResourceInfo info, final List<Pattern> patterns, final List<ResourceKey> list )
    {
        final String path = "/" + info.getResourceName();
        patterns.stream().filter( pattern -> pattern.matcher( path ).find() ).forEach(
            pattern -> list.add( ResourceKey.from( this.appKey, path ) ) );
    }

    private ScriptTestInstance createScriptTestFile( final ResourceKey path )
    {
        final ScriptTestInstance result = new ScriptTestInstance();

        final ScriptExecutor executor = createExecutor( result );
        result.setExecutor( executor );

        this.testInstance.initialize();
        executor.executeRequire( path );
        return result;
    }

    private ScriptExecutor createExecutor( final ScriptTestInstance testFile )
    {
        // Setup configuration
        final ConfigBuilder config = ConfigBuilder.create();
        this.testInstance.setupConfig( config );

        // Setup settings
        final ScriptSettings.Builder settings = ScriptSettings.create();
        this.testInstance.setupSettings( settings );
        settings.globalVariable( "__RUNNER__", testFile );
        settings.debug( new DebugSettingsImpl() );
        settings.binding( Context.class, ContextAccessor::current );

        // Setup services
        final MockServiceRegistry serviceRegistry = new MockServiceRegistry();
        this.testInstance.setupServices( serviceRegistry );

        // Create executor
        final ScriptExecutorImpl executor = new ScriptExecutorImpl();
        executor.setRunMode( RunMode.DEV );
        executor.setScriptSettings( settings.build() );
        executor.setServiceRegistry( serviceRegistry );
        executor.setClassLoader( getClassLoader() );
        executor.setResourceService( createResourceService() );
        executor.setApplication( createApplication() );

        // Initialize and return
        executor.initialize();
        return executor;
    }

    private ResourceService createResourceService()
    {
        return new ClassLoaderResourceService( getClassLoader() );
    }

    private Application createApplication()
    {
        final ApplicationBuilder builder = new ApplicationBuilder();
        builder.classLoader( getClassLoader() );
        builder.urlResolver( new ClassLoaderApplicationUrlResolver( getClassLoader() ) );
        builder.config( ConfigBuilder.create().build() );
        builder.bundle( createBundle() );
        return builder.build();
    }

    private Bundle createBundle()
    {
        final Bundle bundle = Mockito.mock( Bundle.class );

        Mockito.when( bundle.getBundleContext() ).thenReturn( createBundleContext() );
        Mockito.when( bundle.getSymbolicName() ).thenReturn( this.testInstance.getAppKey().toString() );
        Mockito.when( bundle.getVersion() ).thenReturn( Version.valueOf( this.testInstance.getAppVersion() ) );
        Mockito.when( bundle.getState() ).thenReturn( Bundle.ACTIVE );

        final Hashtable<String, String> headers = new Hashtable<>();
        Mockito.when( bundle.getHeaders() ).thenReturn( headers );

        return bundle;
    }

    private BundleContext createBundleContext()
    {
        return Mockito.mock( BundleContext.class );
    }

    private static ClassPath initClassPath()
    {
        try
        {
            return ClassPath.from( getClassLoader() );
        }
        catch ( final Exception e )
        {
            throw new Error( e );
        }
    }

    private static ClassLoader getClassLoader()
    {
        return ScriptRunner.class.getClassLoader();
    }
}
