package com.enonic.xp.launcher.impl.weaver;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.trace.TraceEvent;
import com.enonic.xp.trace.Traced;
import com.enonic.xp.trace.Tracer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TraceWeaverTransformerTest
{
    private RecordingTraceManager manager;

    @BeforeEach
    void setUp()
    {
        this.manager = new RecordingTraceManager();
        Tracer.setManager( this.manager );
    }

    @AfterEach
    void tearDown()
    {
        Tracer.setManager( null );
    }

    @Test
    void returnsValueAndCreatesTrace()
        throws Exception
    {
        final TracedFixtureApi fixture = newWovenFixture();

        assertEquals( "Hello world", fixture.hello( "world" ) );

        final RecordingTraceManager.TestTrace trace = this.manager.singleTrace();
        assertEquals( "fixture.hello", trace.getName() );
        assertEquals( "world", trace.get( "who" ) );
        assertNotNull( trace.getStartTime() );
        assertNotNull( trace.getEndTime() );

        assertEquals( 2, this.manager.events.size() );
        assertEquals( TraceEvent.Type.START, this.manager.events.get( 0 ).getType() );
        assertEquals( TraceEvent.Type.END, this.manager.events.get( 1 ).getType() );
        assertSame( trace, this.manager.events.get( 0 ).getTrace() );
        assertSame( trace, this.manager.events.get( 1 ).getTrace() );
    }

    @Test
    void primitiveArgumentsAndReturn_defaultTraceName()
        throws Exception
    {
        final TracedFixtureApi fixture = newWovenFixture();

        assertEquals( 6L, fixture.add( 1, 2L, 3.5d ) );
        assertEquals( "TracedFixture.add", this.manager.singleTrace().getName() );
    }

    @Test
    void voidMethod()
        throws Exception
    {
        final TracedFixtureApi fixture = newWovenFixture();

        fixture.voidWork();

        assertEquals( 1, fixture.getVoidCalls() );
        assertEquals( "fixture.voidWork", this.manager.singleTrace().getName() );
    }

    @Test
    void staticMethod()
        throws Exception
    {
        final Class<?> wovenClass = wovenFixtureClass();

        final Object result = wovenClass.getMethod( "twice", long.class ).invoke( null, 21L );

        assertEquals( 42L, result );
        assertEquals( "fixture.static", this.manager.singleTrace().getName() );
    }

    @Test
    void checkedExceptionPropagatesUnchanged()
        throws Exception
    {
        final TracedFixtureApi fixture = newWovenFixture();

        final IOException thrown = assertThrows( IOException.class, fixture::failWork );
        assertEquals( "fixture failed", thrown.getMessage() );

        final RecordingTraceManager.TestTrace trace = this.manager.singleTrace();
        assertEquals( "fixture.fail", trace.getName() );
        assertNotNull( trace.getEndTime() );
        assertEquals( TraceEvent.Type.END, this.manager.events.get( this.manager.events.size() - 1 ).getType() );
    }

    @Test
    void nestedTracesGetParent()
        throws Exception
    {
        final TracedFixtureApi fixture = newWovenFixture();

        assertEquals( "Hello nested", fixture.nested( "nested" ) );

        assertEquals( 2, this.manager.traces.size() );
        final RecordingTraceManager.TestTrace parent = this.manager.traces.get( 0 );
        final RecordingTraceManager.TestTrace child = this.manager.traces.get( 1 );
        assertEquals( "fixture.nested", parent.getName() );
        assertEquals( "fixture.hello", child.getName() );
        assertSame( parent, child.getParent() );
        assertEquals( parent.getId(), child.getParentId() );
    }

    @Test
    void disabledTracingPassesThrough()
        throws Exception
    {
        final Class<?> wovenClass = wovenFixtureClass();
        final TracedFixtureApi fixture = (TracedFixtureApi) wovenClass.getConstructor().newInstance();
        Tracer.setManager( null );

        // exercises the woven fast path (no lambda, direct body call) for all return shapes
        assertEquals( "Hello world", fixture.hello( "world" ) );
        fixture.voidWork();
        assertEquals( 1, fixture.getVoidCalls() );
        assertEquals( 6L, fixture.add( 1, 2L, 3.5d ) );
        assertEquals( 84L, fixture.syncTwice( 42L ) );
        assertEquals( List.of( "a", "c" ), fixture.firstAndLast( List.of( "a", "b", "c" ) ) );
        assertEquals( 42L, wovenClass.getMethod( "twice", long.class ).invoke( null, 21L ) );

        assertTrue( this.manager.traces.isEmpty() );
        assertTrue( this.manager.events.isEmpty() );
    }

    @Test
    void synchronizedModifierPreserved()
        throws Exception
    {
        final Class<?> wovenClass = wovenFixtureClass();

        assertTrue( java.lang.reflect.Modifier.isSynchronized( wovenClass.getMethod( "syncTwice", long.class ).getModifiers() ) );

        final TracedFixtureApi fixture = (TracedFixtureApi) wovenClass.getConstructor().newInstance();
        assertEquals( 84L, fixture.syncTwice( 42L ) );
        assertEquals( "fixture.sync", this.manager.singleTrace().getName() );
    }

    @Test
    void oldClassFileVersionIsNotWoven()
        throws Exception
    {
        final byte[] bytes = fixtureBytes();
        // patch major version to 50 (Java 6) - invokedynamic is unavailable there
        bytes[6] = 0;
        bytes[7] = 50;

        assertNull( TraceWeaverTransformer.transform( bytes ) );
    }

    @Test
    void interfaceDefaultMethodIsWoven()
        throws Exception
    {
        final byte[] woven = TraceWeaverTransformer.transform( classBytes( TracedInterfaceFixture.class ) );
        assertNotNull( woven );

        final WovenClassLoader loader =
            new WovenClassLoader( TracedInterfaceFixture.class.getName(), woven, getClass().getClassLoader() );
        final Class<?> wovenInterface = loader.loadClass( TracedInterfaceFixture.class.getName() );

        final Object proxy =
            java.lang.reflect.Proxy.newProxyInstance( loader, new Class<?>[]{wovenInterface}, ( p, method, args ) ->
                java.lang.reflect.InvocationHandler.invokeDefault( p, method, args ) );
        final Object result = wovenInterface.getMethod( "greet", String.class ).invoke( proxy, "world" );

        assertEquals( "Hi world", result );
        assertEquals( "fixture.iface", this.manager.singleTrace().getName() );
    }

    @Test
    void oldInterfaceVersionIsNotWoven()
        throws Exception
    {
        final byte[] bytes = classBytes( TracedInterfaceFixture.class );
        // private interface methods require class-file version 53+ (Java 9)
        bytes[6] = 0;
        bytes[7] = 52;

        assertNull( TraceWeaverTransformer.transform( bytes ) );
    }

    @Test
    void untracedMethodIsNotInstrumented()
        throws Exception
    {
        final TracedFixtureApi fixture = newWovenFixture();

        assertEquals( "plain", fixture.notTraced() );

        assertTrue( this.manager.traces.isEmpty() );
    }

    @Test
    void genericSignatureAndAnnotationPreserved()
        throws Exception
    {
        final Class<?> wovenClass = wovenFixtureClass();

        final Method method = wovenClass.getMethod( "firstAndLast", List.class );
        assertNotNull( method.getAnnotation( Traced.class ) );
        assertEquals( "fixture.generic", method.getAnnotation( Traced.class ).value() );

        final ParameterizedType returnType = (ParameterizedType) method.getGenericReturnType();
        assertEquals( String.class, returnType.getActualTypeArguments()[0] );

        final TracedFixtureApi fixture = (TracedFixtureApi) wovenClass.getConstructor().newInstance();
        assertEquals( List.of( "a", "c" ), fixture.firstAndLast( List.of( "a", "b", "c" ) ) );
        assertEquals( "fixture.generic", this.manager.singleTrace().getName() );
    }

    @Test
    void bodyMovedToSyntheticMethod()
        throws Exception
    {
        final Class<?> wovenClass = wovenFixtureClass();

        final Method body = wovenClass.getDeclaredMethod( "hello" + TraceWeaverTransformer.BODY_METHOD_SUFFIX, String.class );
        assertTrue( body.isSynthetic() );
    }

    @Test
    void transformIsIdempotent()
        throws Exception
    {
        final byte[] woven = TraceWeaverTransformer.transform( fixtureBytes() );
        assertNotNull( woven );

        assertNull( TraceWeaverTransformer.transform( woven ) );
    }

    @Test
    void classWithoutTracedMethodsIsNotTransformed()
        throws Exception
    {
        assertNull( TraceWeaverTransformer.transform( classBytes( WovenClassLoader.class ) ) );
    }

    @Test
    void unwovenFixtureDoesNotTrace()
    {
        // Baseline: without weaving the annotation has no effect.
        final TracedFixture fixture = new TracedFixture();
        assertEquals( "Hello world", fixture.hello( "world" ) );
        assertTrue( this.manager.traces.isEmpty() );
    }

    private TracedFixtureApi newWovenFixture()
        throws Exception
    {
        return (TracedFixtureApi) wovenFixtureClass().getConstructor().newInstance();
    }

    private Class<?> wovenFixtureClass()
        throws Exception
    {
        final byte[] woven = TraceWeaverTransformer.transform( fixtureBytes() );
        assertNotNull( woven );

        final WovenClassLoader loader = new WovenClassLoader( TracedFixture.class.getName(), woven, getClass().getClassLoader() );
        final Class<?> wovenClass = loader.loadClass( TracedFixture.class.getName() );
        assertNotSame( TracedFixture.class, wovenClass );
        return wovenClass;
    }

    static byte[] fixtureBytes()
        throws IOException
    {
        return classBytes( TracedFixture.class );
    }

    private static byte[] classBytes( final Class<?> clazz )
        throws IOException
    {
        try (InputStream stream = clazz.getResourceAsStream( clazz.getSimpleName() + ".class" ))
        {
            assertNotNull( stream );
            return stream.readAllBytes();
        }
    }
}
