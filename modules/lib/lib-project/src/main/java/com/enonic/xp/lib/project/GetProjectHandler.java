package com.enonic.xp.lib.project;

import com.enonic.xp.lib.project.mapper.ProjectMapper;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectPermissions;

public final class GetProjectHandler
    extends BaseProjectHandler
{
    private ProjectName id;

    @Override
    protected ProjectMapper doExecute()
    {
        final Project project = this.projectService.get().get( this.id );

        if ( project == null )
        {
            return null;
        }

        final ProjectPermissions projectPermissions = this.projectService.get().getPermissions( project.getName() );

        final boolean publicRead = this.projectService.get().getPublicRead( project.getName() );

        return ProjectMapper.create().setProject( project ).setProjectPermissions( projectPermissions ).setPublicRead( publicRead ).build();
    }

    public void setId( final String value )
    {
        this.id = ProjectName.from( value );
    }
}
