package com.enonic.xp.portal.sse;

import java.util.UUID;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.sse.SseEndpoint;
import com.enonic.xp.web.sse.SseMessage;

/**
 * Manages Server-Sent Events connections: opens them and sends messages to single clients or groups of clients.
 */
@NullMarked
public interface SseManager
{
    /**
     * Turns the request into an open SSE connection without extra response headers or cookies.
     *
     * @param request the request to answer with an event stream
     * @param endpoint receives the connection's lifecycle events
     * @return the id of the new client connection
     */
    UUID setupSse( WebRequest request, SseEndpoint endpoint );

    /**
     * Turns the request into an open SSE connection and sends the headers and cookies of the given response with it.
     * <p>
     * The status is always 200 and the content type is always {@code text/event-stream}; {@code Content-Type},
     * {@code Content-Length} and {@code Transfer-Encoding} headers of the response are ignored.
     * {@code Cache-Control} defaults to {@code no-store} unless the response sets it.
     *
     * @param request the request to answer with an event stream
     * @param response the response whose headers and cookies are sent
     * @param endpoint receives the connection's lifecycle events
     * @return the id of the new client connection
     */
    UUID setupSse( WebRequest request, WebResponse response, SseEndpoint endpoint );

    /**
     * Sends a message to one client. Does nothing if the client is not connected.
     *
     * @param clientId the client to send to
     * @param message the message to send
     */
    void send( UUID clientId, SseMessage message );

    /**
     * Sends a message to every client in a group.
     *
     * @param group the group to send to
     * @param message the message to send
     */
    void sendToGroup( String group, SseMessage message );

    /**
     * Closes a client connection. Does nothing if the client is not connected.
     *
     * @param clientId the client to disconnect
     */
    void close( UUID clientId );

    /**
     * Tells whether a client is connected.
     *
     * @param clientId the client to check
     * @return {@code true} if the connection is open
     */
    boolean isOpen( UUID clientId );

    /**
     * Counts the clients in a group.
     *
     * @param group the group to count
     * @return the number of connected clients in the group
     */
    int getGroupSize( String group );

    /**
     * Adds a connected client to a group. Does nothing if the client is not connected.
     *
     * @param group the group to join
     * @param clientId the client to add
     */
    void addToGroup( String group, UUID clientId );

    /**
     * Removes a client from a group. Does nothing if the client is not connected.
     *
     * @param group the group to leave
     * @param clientId the client to remove
     */
    void removeFromGroup( String group, UUID clientId );
}
