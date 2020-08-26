package com.enonic.xp.admin.impl.rest.resource.project.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectPermissions;

public final class ModifyPermissionsParamsJson
{
    private final ProjectName name;

    private final ProjectPermissions permissions;

    @JsonCreator
    ModifyPermissionsParamsJson( @JsonProperty("name") final String name,
                                 @JsonProperty("permissions") final ProjectPermissionsJson permissions )
    {
        final ProjectPermissions.Builder permissionsBuilder = ProjectPermissions.create( permissions.getPermissions() );

        this.permissions = permissionsBuilder.build();
        this.name = ProjectName.from( name );
    }

    public ProjectPermissions getPermissions()
    {
        return permissions;
    }

    public ProjectName getName()
    {
        return name;
    }
}
