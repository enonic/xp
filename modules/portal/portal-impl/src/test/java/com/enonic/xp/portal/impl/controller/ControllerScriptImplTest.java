package com.enonic.xp.portal.impl.controller;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.enonic.xp.portal.impl.mapper.SseEventMapper;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.util.GenericValue;
import com.enonic.xp.web.sse.SseEvent;
import com.enonic.xp.web.sse.SseEventType;

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
