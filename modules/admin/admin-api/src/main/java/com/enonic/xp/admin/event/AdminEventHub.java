package com.enonic.xp.admin.event;

import java.util.Map;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.security.PrincipalKeys;

/**
 * Hub for admin events: applications register topics and publish messages to them, admin clients
 * subscribe over one shared websocket per page. The hub owns no topics of its own - every topic is
 * created by an application, and only for the lifetime of the application incarnation that created
 * it. See <a href="https://github.com/enonic/xp/issues/12253">#12253</a>.
 * <p>
 * A topic's canonical name is {@code <application-key>:<name>} - the owning application key is
 * part of the name itself, qualified by the hub, never by the caller. Ownership is therefore
 * structural: applications cannot collide on a name, publish to one another's topics, or take a
 * name over, and two applications using the same local name simply have two topics. Subscribers
 * address topics by the canonical name.
 */
public interface AdminEventHub
{
    /**
     * Registers a topic on this node under {@code owner + ":" + name}. Registering the same name
     * again updates {@code allow} and re-evaluates current subscribers against it; those no longer
     * allowed are denied, which doubles as revocation. The registration is cleared when the owning
     * application stops.
     *
     * @param name  local topic name; must not contain {@code ':'} or whitespace
     * @param allow principals allowed to subscribe; empty means administrators only
     * @param owner the registering application
     * @return the canonical topic name subscribers use: {@code <owner>:<name>}
     * @throws IllegalArgumentException when the name is invalid
     */
    String registerTopic( String name, PrincipalKeys allow, ApplicationKey owner );

    /**
     * Publishes a message to the caller's topic {@code caller + ":" + name}, cluster-wide. The
     * message reaches, on every node, the local sockets holding an acknowledged subscription to
     * the topic, stamped with that node's per-topic sequence number. Delivery is not guaranteed -
     * the sequence numbers make loss on the socket leg countable, nothing more.
     *
     * @param caller  the publishing application; the topic is resolved under its key
     * @param name    local topic name, as passed to {@link #registerTopic}
     * @param message message data; must serialize to JSON
     * @throws IllegalArgumentException when the caller has no such topic registered, or the
     *                                  message is too large or not serializable
     */
    void publish( ApplicationKey caller, String name, Map<String, ?> message );
}
