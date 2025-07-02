package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface ContentModifier
{
    void modify( ModifiableContent edit );
}
