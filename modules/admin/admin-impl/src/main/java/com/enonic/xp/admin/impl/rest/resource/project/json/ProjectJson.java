package com.enonic.xp.admin.impl.rest.resource.project.json;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.admin.impl.json.content.attachment.AttachmentJson;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectPermissions;

public final class ProjectJson
{
    private final String name;

    private final String displayName;

    private final String description;

    private final AttachmentJson icon;

    private final ProjectPermissionsJson permissions;

    public ProjectJson( final Project project, final ProjectPermissions projectPermissions )
    {
        Preconditions.checkArgument( project != null, "Project cannot be null." );
        Preconditions.checkArgument( project.getName() != null, "Project name cannot be null." );

        this.name = project.getName().toString();
        this.displayName = project.getDisplayName();
        this.description = project.getDescription();
        this.icon = project.getIcon() != null ? new AttachmentJson( project.getIcon() ) : null;
        this.permissions = projectPermissions != null ? new ProjectPermissionsJson( projectPermissions ) : null;
    }

    public String getName()
    {
        return name;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getDescription()
    {
        return description;
    }

    public AttachmentJson getIcon()
    {
        return icon;
    }

    public ProjectPermissionsJson getPermissions()
    {
        return permissions;
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
        final ProjectJson that = (ProjectJson) o;
        return Objects.equals( name, that.name ) && Objects.equals( displayName, that.displayName ) &&
            Objects.equals( description, that.description ) && Objects.equals( icon, that.icon ) &&
            Objects.equals( permissions, that.permissions );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, displayName, description, icon, permissions );
    }
}
