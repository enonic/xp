package com.enonic.xp.admin.impl.rest.resource.project.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.admin.impl.rest.resource.project.ProjectReadAccess;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectPermissions;

public final class ModifyPermissionsParamsJson
{
    private final ProjectName name;

    private final ProjectPermissions permissions;

    private final ProjectReadAccess readAccess;

    @JsonCreator
    ModifyPermissionsParamsJson( @JsonProperty("name") final String name,
                                 @JsonProperty("permissions") final ProjectPermissionsJson permissions,
                                 @JsonProperty("readAccess") final ProjectReadAccessJson readAccess )
    {
        this.readAccess = readAccess.getProjectReadAccess();

        final ProjectPermissions.Builder permissionsBuilder = ProjectPermissions.create( permissions.getPermissions() );
        this.readAccess.getPrincipals().forEach( permissionsBuilder::addViewer );

        this.permissions = permissionsBuilder.build();
        this.name = ProjectName.from( name );
    }

    public ProjectPermissions getPermissions()
    {
        return permissions;
    }

    public ProjectReadAccess getReadAccess()
    {
        return readAccess;
    }

    public ProjectName getName()
    {
        return name;
    }
}
