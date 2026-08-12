package com.enonic.xp.admin.event;

import java.util.Map;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.security.PrincipalKeys;

/**
 * Hub for admin events: applications register topics and publish messages to them, admin clients
 * subscribe over one shared websocket per page. The hub owns no topics of its own - every topic is
 * created by an application, and only for the lifetime of the application incarnation that created
 * it. See <a href="https://github.com/enonic/xp/issues/12253">#12253</a>.
 */
public interface AdminEventHub
{
    /**
     * Registers a topic on this node. Topic names are free-form and unscoped by design - prefixing
     * with the application key is a convention, not a rule - so ownership binds at registration:
     * the first application to register a name owns it, only the owner may publish to it, and the
     * registration is cleared when the owning application stops. Registering an already-owned name
     * again updates {@code allow} and re-evaluates current subscribers against it; those no longer
     * allowed are denied, which doubles as revocation.
     *
     * @param name  topic name; free-form, application-key prefix recommended
     * @param allow principals allowed to subscribe; empty means administrators only
     * @param owner the registering application
     * @throws IllegalArgumentException when the name is invalid or owned by another application
     */
    void registerTopic( String name, PrincipalKeys allow, ApplicationKey owner );

    /**
     * Publishes a message to a topic, cluster-wide. The message reaches, on every node, the local
     * sockets holding an acknowledged subscription to the topic, stamped with that node's
     * per-topic sequence number. Delivery is not guaranteed - the sequence numbers make loss on
     * the socket leg countable, nothing more.
     *
     * @param caller  the publishing application; must own the topic
     * @param name    topic name
     * @param message message data; must serialize to JSON
     * @throws IllegalArgumentException when the topic is not registered by {@code caller}, or the
     *                                  message is too large or not serializable
     */
    void publish( ApplicationKey caller, String name, Map<String, ?> message );
}
