package com.enonic.xp.lib.sse;

import java.util.UUID;

import com.enonic.xp.portal.sse.SseManager;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.web.sse.SseMessage;

public final class SseManagerBean
    implements ScriptBean
{
    private SseManager sseManager;

    public void send( final String clientId, final String id, final String event, final String data, final String comment )
    {
        this.sseManager.send( UUID.fromString( clientId ), buildMessage( id, event, data, comment ) );
    }

    public void sendToGroup( final String group, final String id, final String event, final String data, final String comment )
    {
        this.sseManager.sendToGroup( group, buildMessage( id, event, data, comment ) );
    }

    private static SseMessage buildMessage( final String id, final String event, final String data, final String comment )
    {
        final SseMessage.Builder builder = SseMessage.create();
        if ( id != null )
        {
            builder.id( id );
        }
        if ( event != null )
        {
            builder.event( event );
        }
        if ( data != null )
        {
            builder.data( data );
        }
        if ( comment != null )
        {
            builder.comment( comment );
        }
        return builder.build();
    }

    public void close( final String clientId )
    {
        this.sseManager.close( UUID.fromString( clientId ) );
    }

    public void addToGroup( final String group, final String clientId )
    {
        this.sseManager.addToGroup( group, UUID.fromString( clientId ) );
    }

    public void removeFromGroup( final String group, final String clientId )
    {
        this.sseManager.removeFromGroup( group, UUID.fromString( clientId ) );
    }

    public int getGroupSize( final String group )
    {
        return this.sseManager.getGroupSize( group );
    }

    public boolean isOpen( final String clientId )
    {
        return this.sseManager.isOpen( UUID.fromString( clientId ) );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.sseManager = context.getService( SseManager.class ).get();
    }
}
