package com.enonic.xp.admin.impl.rest.resource.project.json;

import java.util.Set;

import com.enonic.xp.project.ProjectPermissions;

public class ProjectPermissionsJson
{
    private final ProjectPermissions permissions;

    public ProjectPermissionsJson( final ProjectPermissions permissions )
    {
        this.permissions = permissions;
    }

    public Set<String> getOwner()
    {
        return this.permissions.getOwner().asStrings();
    }

    public Set<String> getEditor()
    {
        return this.permissions.getEditor().asStrings();
    }

    public Set<String> getAuthor()
    {
        return this.permissions.getAuthor().asStrings();
    }

    public Set<String> getContributor()
    {
        return this.permissions.getContributor().asStrings();
    }
}
