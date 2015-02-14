package com.enonic.xp.core.session;

import com.enonic.xp.core.context.MutableAttributes;

public interface Session
    extends MutableAttributes
{
    public SessionKey getKey();

    public void invalidate();
}
