package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface SyncContentService
{
    void resetInheritance( final ResetContentInheritParams params );
}
