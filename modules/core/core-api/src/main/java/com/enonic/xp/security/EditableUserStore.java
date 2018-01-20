package com.enonic.xp.security;

import com.google.common.annotations.Beta;

@Beta
public final class EditableUserStore
{
    public final UserStore source;

    public UserStoreKey key;

    public String displayName;

    public String description;

    public AuthConfig authConfig;

    public EditableUserStore( final UserStore source )
    {
        this.source = source;
        this.key = source.getKey();
        this.displayName = source.getDisplayName();
        this.description = source.getDescription();
        this.authConfig = source.getAuthConfig();
    }

    public UserStore build()
    {
        return UserStore.create( this.source ).
            key( key ).
            displayName( displayName ).
            description( description ).
            authConfig( authConfig ).
            build();
    }
}
