package com.enonic.wem.api.support;


import java.time.Instant;

import com.enonic.wem.api.security.PrincipalKey;

public interface ChangeTraceable
{
    public Instant getCreatedTime();

    public Instant getModifiedTime();

    public PrincipalKey getCreator();

    public PrincipalKey getModifier();

}
