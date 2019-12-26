package com.enonic.xp.security;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class EditableIdProvider
{
    public final IdProvider source;

    public IdProviderKey key;

    public String displayName;

    public String description;

    public IdProviderConfig idProviderConfig;

    public EditableIdProvider( final IdProvider source )
    {
        this.source = source;
        this.key = source.getKey();
        this.displayName = source.getDisplayName();
        this.description = source.getDescription();
        this.idProviderConfig = source.getIdProviderConfig();
    }

    public IdProvider build()
    {
        return IdProvider.create( this.source ).
            key( key ).
            displayName( displayName ).
            description( description ).
            idProviderConfig( idProviderConfig ).
            build();
    }
}
