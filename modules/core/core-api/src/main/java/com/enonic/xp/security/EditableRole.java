package com.enonic.xp.security;

import java.time.Instant;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class EditableRole
{
    public final Role source;

    public PrincipalKey key;

    public String displayName;

    public Instant modifiedTime;

    public String description;

    public EditableRole( final Role source )
    {
        this.source = source;
        this.displayName = source.getDisplayName();
        this.key = source.getKey();
        this.modifiedTime = source.getModifiedTime();
        this.description = source.getDescription();
    }

    public Role build()
    {
        return Role.create( this.source ).
            displayName( displayName ).
            key( key ).
            modifiedTime( modifiedTime ).
            description( description ).
            build();
    }
}
