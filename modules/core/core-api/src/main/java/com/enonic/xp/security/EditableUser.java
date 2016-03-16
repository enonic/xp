package com.enonic.xp.security;

import java.time.Instant;

import com.google.common.annotations.Beta;

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
            build();
    }
}
