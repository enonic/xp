package com.enonic.xp.admin.impl.rest.resource.security.json;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.security.PathGuard;
import com.enonic.xp.security.UserStoreAuthConfig;

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

    public String getKey()
    {
        return pathGuard.getKey().toString();
    }

    public UserStoreAuthConfigJson getAuthConfig()
    {
        final UserStoreAuthConfig authConfig = pathGuard.getAuthConfig();
        return UserStoreAuthConfigJson.toJson( authConfig );
    }

    public ImmutableSet<String> getPaths()
    {
        return pathGuard.getPaths();
    }
}
