package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface ContentPatcher
{
    void patch( PatchableContent patch );
}
