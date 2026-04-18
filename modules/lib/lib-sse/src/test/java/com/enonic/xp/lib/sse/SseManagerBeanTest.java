package com.enonic.xp.lib.sse;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.portal.sse.SseManager;
import com.enonic.xp.web.sse.SseMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SseManagerBeanTest
{
    private SseManager sseManager;

    private SseManagerBean bean;

    @BeforeEach
    void setup()
        throws Exception
    {
        sseManager = mock( SseManager.class );
        bean = new SseManagerBean();
        final var field = SseManagerBean.class.getDeclaredField( "sseManager" );
        field.setAccessible( true );
        field.set( bean, sseManager );
    }

    @Test
    void send()
    {
        final UUID id = UUID.randomUUID();
        bean.send( id.toString(), "evt-1", "ev", "data", null );
        verify( sseManager ).send( eq( id ), any( SseMessage.class ) );
    }

    @Test
    void send_commentOnly()
    {
        final UUID id = UUID.randomUUID();
        bean.send( id.toString(), null, null, null, "ping" );
        verify( sseManager ).send( eq( id ), any( SseMessage.class ) );
    }

    @Test
    void sendToGroup()
    {
        bean.sendToGroup( "g1", null, "ev", "data", null );
        verify( sseManager ).sendToGroup( eq( "g1" ), any( SseMessage.class ) );
    }

    @Test
    void sendToGroup_commentOnly()
    {
        bean.sendToGroup( "g1", null, null, null, "ping" );
        verify( sseManager ).sendToGroup( eq( "g1" ), any( SseMessage.class ) );
    }

    @Test
    void close()
    {
        final UUID id = UUID.randomUUID();
        bean.close( id.toString() );
        verify( sseManager ).close( id );
    }

    @Test
    void addToGroup()
    {
        final UUID id = UUID.randomUUID();
        bean.addToGroup( "g1", id.toString() );
        verify( sseManager ).addToGroup( "g1", id );
    }

    @Test
    void removeFromGroup()
    {
        final UUID id = UUID.randomUUID();
        bean.removeFromGroup( "g1", id.toString() );
        verify( sseManager ).removeFromGroup( "g1", id );
    }

    @Test
    void getGroupSize()
    {
        when( sseManager.getGroupSize( "g1" ) ).thenReturn( 7 );
        assertEquals( 7, bean.getGroupSize( "g1" ) );
    }

    @Test
    void isOpen()
    {
        final UUID id = UUID.randomUUID();
        when( sseManager.isOpen( id ) ).thenReturn( true );
        assertTrue( bean.isOpen( id.toString() ) );
    }
}
