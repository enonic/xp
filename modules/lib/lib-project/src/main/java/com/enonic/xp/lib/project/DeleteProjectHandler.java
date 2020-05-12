package com.enonic.xp.lib.project;

import com.enonic.xp.project.ProjectName;

public final class DeleteProjectHandler
    extends BaseProjectHandler
{
    private ProjectName name;

    @Override
    protected Boolean doExecute()
    {
        return this.projectService.delete( this.name );
    }

    public void setName( final String value )
    {
        this.name = ProjectName.from( value );
    }
}
