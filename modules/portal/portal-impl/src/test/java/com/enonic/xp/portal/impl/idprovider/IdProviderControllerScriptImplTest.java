package com.enonic.xp.portal.impl.idprovider;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.web.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IdProviderControllerScriptImplTest
{
    @Test
    void execute_readsScriptResultWhileItsContextIsBound()
    {
        // the value a script returns stays guest-owned: reading it once the context slot has been
        // handed to another thread is what fails with "Multi threaded access" under GraalJS, so
        // execution and serialization must share one bound scope
        final AtomicBoolean bound = new AtomicBoolean();
        final ScriptExports exports = boundExports( bound );

        final ScriptValue result = mock( ScriptValue.class );
        when( result.isObject() ).thenAnswer( invocation -> requireBound( bound, true ) );
        when( result.getMember( anyString() ) ).thenAnswer( invocation -> requireBound( bound, null ) );

        when( exports.hasMethod( "get" ) ).thenReturn( true );
        when( exports.executeMethod( eq( "get" ), any() ) ).thenReturn( result );

        final IdProviderControllerScriptImpl script = new IdProviderControllerScriptImpl( exports );

        final PortalResponse response = script.execute( "get", new PortalRequest() );

        assertEquals( HttpStatus.OK, response.getStatus() );
    }

    @Test
    void execute_methodMissing_returnsNotFound()
    {
        final AtomicBoolean bound = new AtomicBoolean();
        final ScriptExports exports = boundExports( bound );
        when( exports.hasMethod( "get" ) ).thenReturn( false );

        final IdProviderControllerScriptImpl script = new IdProviderControllerScriptImpl( exports );

        final PortalResponse response = script.execute( "get", new PortalRequest() );

        assertEquals( HttpStatus.NOT_FOUND, response.getStatus() );
    }

    @Test
    void execute_functionReturnsNothing_returnsNoResponse()
    {
        final AtomicBoolean bound = new AtomicBoolean();
        final ScriptExports exports = boundExports( bound );
        when( exports.hasMethod( "get" ) ).thenReturn( true );
        when( exports.executeMethod( eq( "get" ), any() ) ).thenReturn( null );

        final IdProviderControllerScriptImpl script = new IdProviderControllerScriptImpl( exports );

        assertNull( script.execute( "get", new PortalRequest() ) );
    }

    @Test
    void execute_functionReturnsNonObject_returnsNoResponse()
    {
        final AtomicBoolean bound = new AtomicBoolean();
        final ScriptExports exports = boundExports( bound );

        final ScriptValue result = mock( ScriptValue.class );
        when( result.isObject() ).thenAnswer( invocation -> requireBound( bound, false ) );

        when( exports.hasMethod( "get" ) ).thenReturn( true );
        when( exports.executeMethod( eq( "get" ), any() ) ).thenReturn( result );

        final IdProviderControllerScriptImpl script = new IdProviderControllerScriptImpl( exports );

        assertNull( script.execute( "get", new PortalRequest() ) );
    }

    @Test
    void execute_restoresTheApplicationKeyTheRequestCameWith()
    {
        final AtomicBoolean bound = new AtomicBoolean();
        final ScriptExports exports = boundExports( bound );
        when( exports.hasMethod( "get" ) ).thenReturn( true );

        final PortalRequest request = new PortalRequest();
        final ApplicationKey caller = ApplicationKey.from( "callerapp" );
        request.setApplicationKey( caller );

        new IdProviderControllerScriptImpl( exports ).execute( "get", request );

        assertEquals( caller, request.getApplicationKey() );
    }

    @Test
    void hasMethod_delegatesToExports()
    {
        final ScriptExports exports = mock( ScriptExports.class );
        when( exports.hasMethod( "handle401" ) ).thenReturn( true );

        final IdProviderControllerScriptImpl script = new IdProviderControllerScriptImpl( exports );

        assertTrue( script.hasMethod( "handle401" ) );
        assertFalse( script.hasMethod( "get" ) );
    }

    private static ScriptExports boundExports( final AtomicBoolean bound )
    {
        final ScriptExports exports = mock( ScriptExports.class );
        when( exports.getScript() ).thenReturn( ResourceKey.from( "myapp:/idprovider/idprovider.js" ) );
        when( exports.executeBound( any() ) ).thenAnswer( invocation -> {
            bound.set( true );
            try
            {
                return invocation.getArgument( 0, Function.class ).apply( exports );
            }
            finally
            {
                bound.set( false );
            }
        } );
        return exports;
    }

    private static <T> T requireBound( final AtomicBoolean bound, final T value )
    {
        if ( !bound.get() )
        {
            throw new IllegalStateException( "guest value read outside the bound context scope" );
        }
        return value;
    }
}
