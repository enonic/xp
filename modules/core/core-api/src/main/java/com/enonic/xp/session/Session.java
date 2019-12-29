package com.enonic.xp.session;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.context.MutableAttributes;

@PublicApi
public interface Session
    extends MutableAttributes
{
    SessionKey getKey();

    void invalidate();
}
