package com.enonic.xp.admin.impl.rest.resource.project.json;

import java.util.Objects;
import java.util.Set;

import com.enonic.xp.project.ProjectPermissions;

@SuppressWarnings("UnusedDeclaration")
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

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final ProjectPermissionsJson that = (ProjectPermissionsJson) o;
        return Objects.equals( permissions, that.permissions );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( permissions );
    }
}
