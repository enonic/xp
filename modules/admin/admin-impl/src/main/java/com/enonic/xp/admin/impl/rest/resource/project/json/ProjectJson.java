package com.enonic.xp.admin.impl.rest.resource.project.json;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.admin.impl.json.content.attachment.AttachmentJson;
import com.enonic.xp.admin.impl.rest.resource.project.ProjectReadAccessType;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectPermissions;

@SuppressWarnings("UnusedDeclaration")
public final class ProjectJson
{
    private final String name;

    private final String displayName;

    private final String description;

    private final AttachmentJson icon;

    private final ProjectPermissionsJson permissions;

    private final ProjectReadAccessJson projectReadAccess;

    public ProjectJson( final Project project, final ProjectPermissions projectPermissions, final ProjectReadAccessType readAccessType )
    {
        Preconditions.checkArgument( project != null, "Project cannot be null." );
        Preconditions.checkArgument( project.getName() != null, "Project name cannot be null." );

        this.name = project.getName().toString();
        this.displayName = project.getDisplayName();
        this.description = project.getDescription();
        this.icon = project.getIcon() != null ? new AttachmentJson( project.getIcon() ) : null;
        this.permissions = projectPermissions != null ? new ProjectPermissionsJson( projectPermissions ) : null;
        this.projectReadAccess = readAccessType != null ? new ProjectReadAccessJson( readAccessType, ImmutableList.copyOf(
            projectPermissions.getViewer().getSet() ) ) : null;
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

    public ProjectReadAccessJson getReadAccess()
    {
        return projectReadAccess;
    }
}
