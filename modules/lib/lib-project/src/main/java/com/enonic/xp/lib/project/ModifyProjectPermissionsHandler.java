package com.enonic.xp.lib.project;

import com.enonic.xp.lib.project.mapper.ProjectPermissionsMapper;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectPermissions;
import com.enonic.xp.script.ScriptValue;

public abstract class ModifyProjectPermissionsHandler
    extends BaseProjectHandler
{
    private ProjectName id;

    private ProjectPermissions permissions;

    @Override
    protected ProjectPermissionsMapper doExecute()
    {
        final ProjectPermissions permissionsBeforeUpdate = this.projectService.getPermissions( this.id );

        final ProjectPermissions projectPermissions = this.permissions != null
            ? this.projectService.modifyPermissions( this.id, merge( permissionsBeforeUpdate, this.permissions ) )
            : this.projectService.getPermissions( this.id );

        return new ProjectPermissionsMapper( projectPermissions );
    }

    protected abstract ProjectPermissions merge( ProjectPermissions permissionsBeforeUpdate, ProjectPermissions paramPermissions );

    public void setId( final String value )
    {
        this.id = ProjectName.from( value );
    }

    public void setPermissions( final ScriptValue value )
    {
        this.permissions = buildProjectPermissions( value );
    }
}
