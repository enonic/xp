package com.enonic.xp.admin.event.impl;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.enonic.xp.security.PrincipalKeys;

/**
 * The state of one topic: its registration, its sequence numbering, and the sockets holding an
 * acknowledged subscription to it. Guarded by {@link #lock} for anything that must be atomic
 * against stamp-and-send.
 */
final class TopicState
{
    final Object lock = new Object();

    final AtomicLong seq = new AtomicLong();

    // socket ids, this node only; delivery reaches these and nothing else
    final Set<String> subscribers = ConcurrentHashMap.newKeySet();

    // subscribe ACL; null while unregistered
    volatile PrincipalKeys allow;
}
