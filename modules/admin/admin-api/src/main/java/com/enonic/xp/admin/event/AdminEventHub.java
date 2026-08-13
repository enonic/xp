package com.enonic.xp.admin.event;

import org.jspecify.annotations.NullMarked;

/**
 * Registry and transport for admin event topics.
 * <p>
 * A topic is identified by its canonical name {@code <application-key>:<name>}: the key of the
 * owning application, {@code ':'}, and the local name given at registration. Registration and
 * publishing resolve the canonical name from the supplied application key and local name.
 * Subscribers address topics by the canonical name. The registration is cleared when the owning
 * application stops: publishing then fails and new subscriptions are denied, while existing
 * subscriptions and the topic's sequence numbering persist and resume when the topic is
 * registered again.
 */
@NullMarked
public interface AdminEventHub
{
    /**
     * Sets the state of the topic {@code owner + ":" + name} on this node.
     * <p>
     * A non-empty {@code allow} registers the topic, or updates its {@code allow} when already
     * registered; on update, current subscribers are re-evaluated against the new {@code allow},
     * and subscribers no longer allowed are denied and removed. An empty {@code allow} clears the
     * registration, with the same effect as the owning application stopping.
     *
     * @param params topic parameters; {@code name} is the local topic name: 1-255 characters,
     *               no {@code ':'}, no whitespace
     * @return the canonical topic name
     * @throws IllegalArgumentException if {@code name} is invalid
     */
    String setTopic( SetTopicParams params );


    /**
     * Publishes a message to the topic {@code caller + ":" + name}. On every cluster node the
     * message is delivered to the local sockets holding an acknowledged subscription to the
     * topic, stamped with that node's per-topic sequence number. Delivery is not guaranteed.
     *
     * @param params publish parameters
     * @throws IllegalArgumentException if the topic is not registered by {@code caller}, or the
     *                                  message is too large
     */
    void publish( PublishMessageParams params );
}
