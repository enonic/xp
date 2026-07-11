package com.enonic.xp.portal.impl.controller;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.mapper.SseEventMapper;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.trace.TestTrace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.util.GenericValue;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.sse.SseEvent;
import com.enonic.xp.web.sse.SseEventType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ControllerScriptImplTest
{
    @Test
    void execute_recordsScriptTraceAttribute()
    {
        final ScriptExports exports = mock( ScriptExports.class );
        when( exports.getScript() ).thenReturn( ResourceKey.from( "myapp:/site/controllers/controller.js" ) );

        final ControllerScriptImpl script = new ControllerScriptImpl( exports );

        final PortalRequest request = new PortalRequest();
        request.setMethod( HttpMethod.GET );

        // outside OSGi the @Traced wrapper is inert; a manually bound trace exercises the attribute enrichment code
        final TestTrace trace = TestTrace.of( "controllerScript" );
        final PortalResponse response = Tracer.trace( trace, () -> script.execute( request ) );

        assertEquals( HttpStatus.METHOD_NOT_ALLOWED, response.getStatus() );
        final Object scriptAttribute = trace.get( "script" );
        assertInstanceOf( String.class, scriptAttribute );
        assertTrue( ( (String) scriptAttribute ).endsWith( "/site/controllers/controller.js" ) );
    }

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
