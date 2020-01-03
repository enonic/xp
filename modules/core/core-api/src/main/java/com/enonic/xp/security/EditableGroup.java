package com.enonic.xp.security;

import java.time.Instant;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class EditableGroup
{
    public final Group source;

    public PrincipalKey key;

    public String displayName;

    public Instant modifiedTime;

    public String description;

    public EditableGroup( final Group source )
    {
        this.source = source;
        this.displayName = source.getDisplayName();
        this.key = source.getKey();
        this.modifiedTime = source.getModifiedTime();
        this.description = source.getDescription();
    }

    public Group build()
    {
        return Group.create( this.source ).
            displayName( displayName ).
            key( key ).
            modifiedTime( modifiedTime ).
            description( description ).
            build();
    }
}
