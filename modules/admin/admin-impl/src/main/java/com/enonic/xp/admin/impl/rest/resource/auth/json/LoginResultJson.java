package com.enonic.xp.admin.impl.rest.resource.auth.json;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.admin.impl.app.AdminApplication;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

public final class LoginResultJson
{
    private final AuthenticationInfo authenticationInfo;

    private final List<AdminApplication> applications;

    private final String message;

    public LoginResultJson( final AuthenticationInfo authenticationInfo, final List<AdminApplication> applications )
    {
        this.authenticationInfo = authenticationInfo;
        this.applications = applications;
        this.message = null;
    }

    public LoginResultJson( final AuthenticationInfo authenticationInfo )
    {
        this.authenticationInfo = authenticationInfo;
        this.applications = ImmutableList.of();
        this.message = null;
    }

    public LoginResultJson( final AuthenticationInfo authenticationInfo, final String message )
    {
        this.authenticationInfo = authenticationInfo;
        this.applications = ImmutableList.of();
        this.message = message;
    }

    public boolean isAuthenticated()
    {
        return authenticationInfo.isAuthenticated();
    }

    public UserJson getUser()
    {
        final User user = authenticationInfo.getUser();
        return user == null ? null : new UserJson( user );
    }

    public String[] getPrincipals()
    {
        final PrincipalKeys principals = this.authenticationInfo.getPrincipals();
        if ( principals == null )
        {
            return new String[0];
        }
        return principals.stream().map( PrincipalKey::toString ).toArray( String[]::new );
    }

    public String[] getApplications()
    {
        return applications.stream().map( AdminApplication::getId ).toArray( String[]::new );
    }

    public String getMessage()
    {
        return message;
    }
}
