package com.enonic.wem.api.session;

import com.enonic.wem.api.context.MutableAttributes;

public interface Session
    extends MutableAttributes
{
    public SessionKey getKey();

    public void invalidate();
}
