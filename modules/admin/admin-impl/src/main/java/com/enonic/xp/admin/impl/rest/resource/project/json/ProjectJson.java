package com.enonic.xp.admin.impl.rest.resource.project.json;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.admin.impl.json.content.attachment.AttachmentJson;
import com.enonic.xp.admin.impl.rest.resource.project.layer.json.ContentLayerJson;
import com.enonic.xp.project.Project;

public final class ProjectJson
{
    private final String name;

    private final String displayName;

    private final String description;

    private final AttachmentJson icon;

    private final List<ContentLayerJson> layers;

    private final ProjectPermissionsJson permissions;

    public ProjectJson( final Project project )
    {
        Preconditions.checkArgument( project != null, "Project cannot be null." );
        Preconditions.checkArgument( project.getName() != null, "Project name cannot be null." );

        this.name = project.getName().toString();
        this.displayName = project.getDisplayName();
        this.description = project.getDescription();
        this.icon = project.getIcon() != null ? new AttachmentJson( project.getIcon() ) : null;
        this.layers = project.getLayers() != null ? project.getLayers().stream().
            map( ContentLayerJson::new ).
            collect( Collectors.toList() ) : null;
        this.permissions = project.getPermissions() != null ? new ProjectPermissionsJson( project.getPermissions() ) : null;
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

    public List<ContentLayerJson> getLayers()
    {
        return layers;
    }

    public ProjectPermissionsJson getPermissions()
    {
        return permissions;
    }
}
