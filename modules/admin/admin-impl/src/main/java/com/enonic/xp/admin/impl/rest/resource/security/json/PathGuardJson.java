package com.enonic.xp.admin.impl.rest.resource.security.json;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.security.AuthConfig;
import com.enonic.xp.security.PathGuard;

@SuppressWarnings("UnusedDeclaration")
public class PathGuardJson
{
    private final PathGuard pathGuard;

    public PathGuardJson( final PathGuard pathGuard )
    {

        this.pathGuard = pathGuard;
    }

    public String getDisplayName()
    {
        return pathGuard.getDisplayName();
    }

    public String getDescription()
    {
        return pathGuard.getDescription();
    }

    public String getKey()
    {
        return pathGuard.getKey().toString();
    }

    public AuthConfigJson getAuthConfig()
    {
        final AuthConfig authConfig = pathGuard.getAuthConfig();
        return AuthConfigJson.toJson( authConfig );
    }

    public ImmutableSet<String> getPaths()
    {
        return pathGuard.getPaths();
    }
}
