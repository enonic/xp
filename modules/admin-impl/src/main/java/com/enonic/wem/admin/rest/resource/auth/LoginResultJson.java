package com.enonic.wem.admin.rest.resource.auth;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.security.User;
import com.enonic.wem.api.security.auth.AuthenticationInfo;

public final class LoginResultJson
{
    private final AuthenticationInfo authenticationInfo;

    private final List<AdminApplication> applications;

    public LoginResultJson( final AuthenticationInfo authenticationInfo, final List<AdminApplication> applications )
    {
        this.authenticationInfo = authenticationInfo;
        this.applications = applications;
    }

    public LoginResultJson( final AuthenticationInfo authenticationInfo )
    {
        this.authenticationInfo = authenticationInfo;
        this.applications = ImmutableList.of();
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

    public String[] getApplications()
    {
        return applications.stream().map( AdminApplication::getId ).toArray( String[]::new );
    }
}
