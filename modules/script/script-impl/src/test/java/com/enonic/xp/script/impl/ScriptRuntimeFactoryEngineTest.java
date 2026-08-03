package com.enonic.xp.script.impl;

import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.script.impl.standard.ScriptRuntimeImpl;
import com.enonic.xp.script.runtime.BootstrapParams;
import com.enonic.xp.script.runtime.ScriptSettings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;

/**
 * Which engine an application runs on is decided per bundle, from its {@code X-Script-Engine}
 * header, when its executor is built — the point where an unregistered or half-started
 * application must also be refused.
 */
class ScriptRuntimeFactoryEngineTest
{
    private static final ApplicationKey APP_KEY = ApplicationKey.from( "myapplication" );

    private static final ResourceKey SCRIPT = ResourceKey.from( APP_KEY, "/engine-test.js" );

    private BundleContext bundleContext;

    private ScriptRuntimeFactoryImpl factory;

    private Application application;

    @BeforeEach
    void setUp()
        throws Exception
    {
        this.bundleContext = Mockito.mock( BundleContext.class );
        Mockito.lenient()
            .when( this.bundleContext.createFilter( anyString() ) )
            .thenAnswer( invocation -> FrameworkUtil.createFilter( invocation.getArgument( 0 ) ) );
        Mockito.lenient().when( this.bundleContext.getServiceReferences( anyString(), nullable( String.class ) ) ).thenReturn( null );

        final ResourceService resourceService = Mockito.mock( ResourceService.class );
        Mockito.lenient().when( resourceService.getResource( Mockito.any() ) ).thenAnswer( invocation -> {
            final ResourceKey key = invocation.getArgument( 0 );
            final URL url = getClass().getResource( "/" + key.getApplicationKey() + key.getPath() );
            return new UrlResource( key, url );
        } );

        this.factory = new ScriptRuntimeFactoryImpl( this.bundleContext, resourceService );
    }

    @AfterEach
    void destroy()
    {
        this.factory.destroy();
    }

    @SuppressWarnings("unchecked")
    private ServiceReference<Application> register( final String scriptEngineHeader, final boolean started )
    {
        final Dictionary<String, String> headers = new Hashtable<>();
        if ( scriptEngineHeader != null )
        {
            headers.put( "X-Script-Engine", scriptEngineHeader );
        }

        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.lenient().when( bundle.getHeaders() ).thenReturn( headers );
        Mockito.lenient().when( bundle.getBundleContext() ).thenReturn( this.bundleContext );
        Mockito.lenient().when( bundle.getVersion() ).thenReturn( Version.valueOf( "1.0.0" ) );

        final Application application = Mockito.mock( Application.class );
        Mockito.lenient().when( application.getKey() ).thenReturn( APP_KEY );
        Mockito.lenient().when( application.getVersion() ).thenReturn( com.enonic.xp.util.Version.emptyVersion );
        Mockito.lenient().when( application.getClassLoader() ).thenReturn( getClass().getClassLoader() );
        Mockito.lenient().when( application.isStarted() ).thenReturn( started );
        Mockito.lenient().when( application.getConfig() ).thenReturn( ConfigBuilder.create().build() );

        final ServiceReference<Application> reference = Mockito.mock( ServiceReference.class );
        Mockito.lenient().when( reference.getBundle() ).thenReturn( bundle );
        Mockito.when( this.bundleContext.getService( reference ) ).thenReturn( application );

        this.application = application;
        this.factory.addingService( reference );
        return reference;
    }

    /**
     * A registration present in the registry that this factory's tracker callback has not been
     * delivered for yet.
     */
    @SuppressWarnings("unchecked")
    private ServiceReference<Application> registerWithoutTrackerCallback( final String scriptEngineHeader )
        throws Exception
    {
        final ServiceReference<Application> reference = register( scriptEngineHeader, true );
        // undo the callback: the registry still has it, this factory has not seen it
        this.factory.removedService( reference, this.application );

        Mockito.when( this.bundleContext.getServiceReferences( Application.class, "(name=" + APP_KEY + ")" ) )
            .thenReturn( List.of( reference ) );
        return reference;
    }

    private String engineOf( final ScriptRuntimeImpl runtime )
    {
        // top-level executions wait on the bootstrap gate, which only bootstrap() opens
        runtime.bootstrap( BootstrapParams.create().application( APP_KEY ).build() );
        return (String) runtime.executeMethod( SCRIPT, "engine" );
    }

    @Test
    void bundleHeaderSelectsGraalJs()
    {
        register( "GraalJS", true );
        assertEquals( "graal", engineOf( this.factory.doCreate( ScriptSettings.create().build() ) ) );
    }

    @Test
    void bundleHeaderSelectsNashorn()
    {
        register( "Nashorn", true );
        assertEquals( "nashorn", engineOf( this.factory.doCreate( ScriptSettings.create().build() ) ) );
    }

    @Test
    void withoutAHeaderTheInstallationDefaultApplies()
    {
        register( null, true );

        final String expected =
            "GraalJS".equalsIgnoreCase( System.getProperty( "xp.script-engine", "Nashorn" ) ) ? "graal" : "nashorn";
        assertEquals( expected, engineOf( this.factory.doCreate( ScriptSettings.create().build() ) ) );
    }

    @Test
    void anApplicationIsResolvedBeforeItsTrackerCallbackArrives()
        throws Exception
    {
        registerWithoutTrackerCallback( "GraalJS" );

        // bootstrap is driven by a second Application tracker, and OSGi orders neither against the
        // other: resolving has to consult the registry, not just what this tracker has recorded
        final ScriptRuntimeImpl runtime = this.factory.doCreate( ScriptSettings.create().build() );
        assertEquals( "graal", engineOf( runtime ) );
    }

    @Test
    void anApplicationTheRegistryDoesNotHaveIsStillRefused()
        throws Exception
    {
        final ServiceReference<Application> reference = registerWithoutTrackerCallback( "GraalJS" );
        Mockito.when( this.bundleContext.getServiceReferences( Application.class, "(name=" + APP_KEY + ")" ) )
            .thenReturn( List.of() );

        assertFalse( this.factory.doCreate( ScriptSettings.create().build() ).hasScript( SCRIPT ) );
        assertNotNull( reference );
    }

    @Test
    void anUnknownEngineIsRejected()
    {
        register( "Rhino", true );

        final ScriptRuntimeImpl runtime = this.factory.doCreate( ScriptSettings.create().build() );
        final IllegalArgumentException e = assertThrows( IllegalArgumentException.class, () -> runtime.hasScript( SCRIPT ) );
        assertEquals( "Unsupported script engine Rhino", e.getMessage() );
    }

    @Test
    void anApplicationThatIsNotRegisteredHasNoScripts()
    {
        // nothing registered: resolving an executor fails, and hasScript answers for the absence
        assertFalse( this.factory.doCreate( ScriptSettings.create().build() ).hasScript( SCRIPT ) );
    }

    @Test
    void anApplicationThatHasNotStartedHasNoScripts()
    {
        register( "GraalJS", false );
        assertFalse( this.factory.doCreate( ScriptSettings.create().build() ).hasScript( SCRIPT ) );
    }

    @Test
    void aRegisteredApplicationResolvesItsScripts()
    {
        register( "GraalJS", true );

        final ScriptRuntimeImpl runtime = this.factory.doCreate( ScriptSettings.create().build() );
        assertTrue( runtime.hasScript( SCRIPT ) );
        assertFalse( runtime.hasScript( ResourceKey.from( APP_KEY, "/no-such-script.js" ) ) );
    }

    @Test
    void anUnregisteredApplicationStopsResolving()
    {
        final ServiceReference<Application> reference = register( "GraalJS", true );
        final ScriptRuntimeImpl runtime = this.factory.doCreate( ScriptSettings.create().build() );
        assertTrue( runtime.hasScript( SCRIPT ) );

        this.factory.removedService( reference, this.application );

        // the incarnation is gone, so the executor built from it is not reused
        assertFalse( runtime.hasScript( SCRIPT ) );
    }

    @Test
    @SuppressWarnings("unchecked")
    void aForeignRegistrationDoesNotUnregisterTheApplication()
    {
        register( "GraalJS", true );
        final ScriptRuntimeImpl runtime = this.factory.doCreate( ScriptSettings.create().build() );
        assertTrue( runtime.hasScript( SCRIPT ) );

        // same application object, a different registration: on a reconfigure this is the
        // predecessor's callback arriving, and it must not retire the current incarnation
        this.factory.removedService( Mockito.mock( ServiceReference.class ), this.application );

        assertTrue( runtime.hasScript( SCRIPT ) );
    }
}
