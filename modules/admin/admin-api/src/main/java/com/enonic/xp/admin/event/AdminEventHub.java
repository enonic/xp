package com.enonic.xp.admin.event;

import java.util.Map;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.security.PrincipalKeys;

/**
 * Registry and transport for admin event topics.
 * <p>
 * A topic is identified by its canonical name {@code <application-key>:<name>}: the key of the
 * owning application, {@code ':'}, and the local name given at registration. Registration and
 * publishing resolve the canonical name from the supplied application key and local name.
 * Subscribers address topics by the canonical name. A topic exists from registration until the
 * owning application stops.
 */
public interface AdminEventHub
{
    /**
     * Registers the topic {@code owner + ":" + name} on this node, or updates its {@code allow}
     * when already registered. On update, current subscribers are re-evaluated against the new
     * {@code allow}; subscribers no longer allowed are denied and removed.
     *
     * @param name  local topic name: 1-255 characters, no {@code ':'}, no whitespace
     * @param allow principals allowed to subscribe; empty allows administrators only
     * @param owner owning application
     * @return the canonical topic name
     * @throws IllegalArgumentException if {@code name} is invalid
     */
    String registerTopic( String name, PrincipalKeys allow, ApplicationKey owner );

    /**
     * Publishes a message to the topic {@code caller + ":" + name}. On every cluster node the
     * message is delivered to the local sockets holding an acknowledged subscription to the
     * topic, stamped with that node's per-topic sequence number. Delivery is not guaranteed.
     *
     * @param caller  publishing application
     * @param name    local topic name, as passed to {@link #registerTopic}
     * @param message message data; must serialize to JSON
     * @throws IllegalArgumentException if the topic is not registered by {@code caller}, or the
     *                                  message is too large or not serializable
     */
    void publish( ApplicationKey caller, String name, Map<String, ?> message );
}
