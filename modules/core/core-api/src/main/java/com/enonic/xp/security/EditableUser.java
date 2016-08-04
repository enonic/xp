package com.enonic.xp.security;

import java.time.Instant;
import java.util.Map;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.xp.data.PropertySet;

@Beta
public final class EditableUser
{
    public final User source;

    public String email;

    public String login;

    public String authenticationHash;

    public boolean loginDisabled;

    public PrincipalKey key;

    public String displayName;

    public Instant modifiedTime;

    public Map<String, PropertySet> extraDataMap;

    public EditableUser( final User source )
    {
        this.source = source;
        this.displayName = source.getDisplayName();
        this.email = source.getEmail();
        this.login = source.getLogin();
        this.authenticationHash = source.getAuthenticationHash();
        this.loginDisabled = source.isDisabled();
        this.key = source.getKey();
        this.modifiedTime = source.getModifiedTime();
        this.extraDataMap = Maps.newHashMap( source.getExtraDataMap() );
    }

    public User build()
    {
        return User.create( this.source ).
            displayName( displayName ).
            email( email ).
            login( login ).
            authenticationHash( authenticationHash ).
            key( key ).
            modifiedTime( modifiedTime ).
            putAllExtraDataMap( ImmutableMap.copyOf( this.extraDataMap ) ).
            build();
    }
}
