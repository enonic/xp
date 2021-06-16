package com.enonic.xp.admin.impl.rest.resource.content.json;


public class EffectivePermissionJson
{
    private final String access;

    private final EffectivePermissionAccessJson permissionAccessJson;

    public EffectivePermissionJson( final String access, final EffectivePermissionAccessJson permissionAccessJson )
    {
        this.access = access;
        this.permissionAccessJson = permissionAccessJson;
    }

    public String getAccess()
    {
        return access;
    }

    public EffectivePermissionAccessJson getPermissionAccessJson()
    {
        return permissionAccessJson;
    }
}
