package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface SyncContentService
{
    void resetInheritance( ResetContentInheritParams params );

    void syncProject( ProjectSyncParams params );
}
