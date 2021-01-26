package com.enonic.xp.core.impl.content;

public interface ContentEventsSynchronizer
    extends ContentSynchronizer
{
    void sync( ContentEventsSyncParams params );
}
