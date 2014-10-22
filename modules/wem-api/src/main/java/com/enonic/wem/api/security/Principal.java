package com.enonic.wem.api.security;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class Principal
{
    private final PrincipalKey key;

    private final String displayName;

    protected Principal( final PrincipalKey principalKey, final String displayName )
    {
        this.key = checkNotNull( principalKey, "Principal key cannot be null" );
        this.displayName = checkNotNull( displayName, "Principal display name cannot be null" );
    }

    public PrincipalKey getKey()
    {
        return key;
    }

    public String getDisplayName()
    {
        return displayName;
    }

}
