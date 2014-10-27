package com.enonic.wem.api.security.acl;

public enum Permission
{

    READ,
    CREATE,
    MODIFY,
    DELETE,
    PUBLISH,
    READ_PERMISSIONS,
    WRITE_PERMISSIONS;

    private final String id;

    Permission()
    {
        this.id = this.toString().toLowerCase();
    }

    public String id()
    {
        return id;
    }

}
