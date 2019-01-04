package com.enonic.xp.security;

import com.google.common.annotations.Beta;

@Beta
public final class EditableUserStore
{
    public final UserStore source;

    public UserStoreKey key;

    public String displayName;

    public String description;

    public IdProviderConfig idProviderConfig;

    public EditableUserStore( final UserStore source )
    {
        this.source = source;
        this.key = source.getKey();
        this.displayName = source.getDisplayName();
        this.description = source.getDescription();
        this.idProviderConfig = source.getIdProviderConfig();
    }

    public UserStore build()
    {
        return UserStore.create( this.source ).
            key( key ).
            displayName( displayName ).
            description( description ).
            idProviderConfig( idProviderConfig ).
            build();
    }
}
