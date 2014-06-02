package com.enonic.wem.api.support;


import java.time.Instant;

import com.enonic.wem.api.account.UserKey;

public interface ChangeTraceable
{
    public Instant getCreatedTime();

    public Instant getModifiedTime();

    public UserKey getCreator();

    public UserKey getModifier();

}
