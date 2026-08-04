package com.enonic.xp.portal.impl.controller;

import java.util.UUID;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.impl.mapper.SseEventMapper;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.util.GenericValue;
import com.enonic.xp.web.sse.SseEvent;
import com.enonic.xp.web.sse.SseEventType;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ControllerScriptImplTest
{
    @Test
    void onSseEvent_methodPresent_invokesScript()
    {
        final ScriptExports exports = mock( ScriptExports.class );
        when( exports.hasMethod( "sseEvent" ) ).thenReturn( true );

        final ControllerScriptImpl script = new ControllerScriptImpl( exports );
        final SseEvent event = SseEvent.create()
            .type( SseEventType.OPEN )
            .clientId( UUID.randomUUID() )
            .attributes( GenericValue.newObject().build() )
            .build();

        script.onSseEvent( event );

        verify( exports ).executeMethod( eq( "sseEvent" ), any( SseEventMapper.class ) );
    }

    @Test
    void executeBound_wrapsBoundExports()
    {
        final ScriptExports exports = mock( ScriptExports.class );
        final ScriptExports boundExports = mock( ScriptExports.class );
        when( exports.executeBound( any() ) ).thenAnswer(
            invocation -> invocation.getArgument( 0, Function.class ).apply( boundExports ) );

        final ControllerScriptImpl script = new ControllerScriptImpl( exports );

        assertNotSame( script, script.executeBound( bound -> bound ) );
    }

    @Test
    void executeBound_sameExports_passesSameInstance()
    {
        final ScriptExports exports = mock( ScriptExports.class );
        when( exports.executeBound( any() ) ).thenAnswer(
            invocation -> invocation.getArgument( 0, Function.class ).apply( exports ) );

        final ControllerScriptImpl script = new ControllerScriptImpl( exports );

        assertSame( script, script.executeBound( bound -> bound ) );
    }

    @Test
    void retainAndRelease_delegateToExports()
    {
        final ScriptExports exports = mock( ScriptExports.class );
        final ControllerScriptImpl script = new ControllerScriptImpl( exports );

        script.retain();
        verify( exports ).retain();

        script.release();
        verify( exports ).release();
    }

    @Test
    void interfaceDefaults_areNoOps()
    {
        // engines without pooling rely on the interface defaults: bound scopes receive the
        // controller itself and retain/release do nothing
        final ControllerScript script = request -> null;

        assertSame( script, script.executeBound( bound -> bound ) );
        script.retain();
        script.release();
    }

    @Test
    void onSseEvent_methodAbsent_isNoOp()
    {
        final ScriptExports exports = mock( ScriptExports.class );
        when( exports.hasMethod( "sseEvent" ) ).thenReturn( false );

        final ControllerScriptImpl script = new ControllerScriptImpl( exports );
        final SseEvent event = SseEvent.create()
            .type( SseEventType.OPEN )
            .clientId( UUID.randomUUID() )
            .attributes( GenericValue.newObject().build() )
            .build();

        script.onSseEvent( event );

        verify( exports, never() ).executeMethod( any(), any() );
    }
}
