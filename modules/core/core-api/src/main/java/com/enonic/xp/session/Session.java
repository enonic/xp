package com.enonic.xp.session;

import com.enonic.xp.context.MutableAttributes;


public interface Session
    extends MutableAttributes
{
    SessionKey getKey();

    void invalidate();
}
