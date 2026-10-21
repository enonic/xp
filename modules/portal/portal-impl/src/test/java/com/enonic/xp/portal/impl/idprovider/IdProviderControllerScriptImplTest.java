package com.enonic.xp.portal.impl.idprovider;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.web.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

        final ScriptValue result = mock( ScriptValue.class );
        when( result.isObject() ).thenAnswer( invocation -> requireBound( bound, true ) );
        when( result.getMember( anyString() ) ).thenAnswer( invocation -> requireBound( bound, null ) );

        final ScriptExports exports = mock( ScriptExports.class );
        when( exports.getScript() ).thenReturn( ResourceKey.from( "myapp:/idprovider/idprovider.js" ) );
        when( exports.hasMethod( "get" ) ).thenReturn( true );
        when( exports.executeMethod( eq( "get" ), any() ) ).thenReturn( result );
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

        final IdProviderControllerScriptImpl script = new IdProviderControllerScriptImpl( exports );

        final PortalResponse response = script.execute( "get", new PortalRequest() );

        assertEquals( HttpStatus.OK, response.getStatus() );
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
