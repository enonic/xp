package com.enonic.xp.portal.universalapi;

import com.enonic.xp.web.sse.SseEvent;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.websocket.WebSocketEvent;

public interface UniversalApiHandler
{
    WebResponse handle( WebRequest request );

    /**
     * Handles a CORS preflight ({@code OPTIONS}) request. A preflight is sent without credentials, so it is not
     * authorized against the allowed principals of the API and is never passed to {@link #handle(WebRequest)}.
     * Responds with {@link HttpStatus#METHOD_NOT_ALLOWED} by default, which results in the default {@code OPTIONS} response.
     */
    default WebResponse handlePreflight( WebRequest request )
    {
        return WebResponse.create().status( HttpStatus.METHOD_NOT_ALLOWED ).build();
    }

    default void onSocketEvent( WebSocketEvent event )
    {
    }

    default void onSseEvent( SseEvent event )
    {
    }
}
