package com.enonic.xp.session;

import com.google.common.annotations.Beta;

import com.enonic.xp.context.MutableAttributes;

@Beta
public interface Session
    extends MutableAttributes
{
    SessionKey getKey();

    void invalidate();
}
