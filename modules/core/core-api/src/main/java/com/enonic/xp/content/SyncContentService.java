package com.enonic.xp.content;

public interface SyncContentService
{
    void resetInheritance( ResetContentInheritParams params );

    void syncProject( ProjectSyncParams params );
}
