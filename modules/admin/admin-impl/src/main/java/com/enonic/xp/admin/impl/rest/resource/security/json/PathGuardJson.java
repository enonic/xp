package com.enonic.xp.admin.impl.rest.resource.security.json;

import com.google.common.collect.ImmutableSet;

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

    public String getUserStoreKey()
    {
        return pathGuard.getUserStoreKey() == null ? null : pathGuard.getUserStoreKey().toString();
    }

    public Boolean isPassive()
    {
        return pathGuard.isPassive();
    }

    public ImmutableSet<String> getPaths()
    {
        return pathGuard.getPaths();
    }
}
