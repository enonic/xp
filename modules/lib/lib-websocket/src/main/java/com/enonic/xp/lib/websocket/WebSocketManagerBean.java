package com.enonic.xp.lib.websocket;

public final class WebSocketManagerBean
    extends AbstractWebSocketBean
{
    public void addToGroup( final String group, final String id )
    {
        this.webSocketManager.addToGroup( group, id );
    }

    public void removeFromGroup( final String group, final String id )
    {
        this.webSocketManager.removeFromGroup( group, id );
    }

    public void sendToGroup( final String group, final String message )
    {
        this.webSocketManager.sendToGroup( group, message );
    }

    public long getGroupSize( final String group )
    {
        return this.webSocketManager.getGroupSize( group );
    }

    public void send( final String id, final String message )
    {
        this.webSocketManager.send( id, message );
    }
}
