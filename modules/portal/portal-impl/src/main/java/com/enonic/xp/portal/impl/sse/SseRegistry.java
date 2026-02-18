package com.enonic.xp.portal.impl.sse;

import java.util.stream.Stream;

interface SseRegistry
{
    void add( SseEntry entry );

    void remove( SseEntry entry );

    SseEntry getById( String id );

    Stream<SseEntry> getByGroup( String group );
}
