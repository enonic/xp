package com.enonic.xp.lib.project;

import com.enonic.xp.lib.project.mapper.ProjectPermissionsMapper;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectPermissions;
import com.enonic.xp.script.ScriptValue;

public abstract class ModifyProjectPermissionsHandler
    extends BaseProjectHandler
{
    private ProjectName name;

    private ProjectPermissions permissions;

    @Override
    protected ProjectPermissionsMapper doExecute()
    {
        final ProjectPermissions permissionsBeforeUpdate = this.projectService.getPermissions( this.name );

        final ProjectPermissions projectPermissions = this.permissions != null ? this.projectService.modifyPermissions( this.name, merge(
            permissionsBeforeUpdate, this.permissions ) ) : this.projectService.getPermissions( this.name );

        return new ProjectPermissionsMapper( projectPermissions );
    }

    protected abstract ProjectPermissions merge( final ProjectPermissions permissionsBeforeUpdate,
                                                 final ProjectPermissions paramPermissions );

    public void setName( final String value )
    {
        this.name = ProjectName.from( value );
    }

    public void setPermissions( final ScriptValue value )
    {
        this.permissions = buildProjectPermissions( value );
    }
}
