package com.enonic.xp.lib.project;

import java.util.Objects;

import com.enonic.xp.project.ProjectName;

public final class DeleteProjectHandler
    extends BaseProjectHandler
{
    private ProjectName id;

    @Override
    protected Boolean doExecute()
    {
        return this.projectService.get().delete( this.id );
    }

    public void setId( final String value )
    {
        this.id = ProjectName.from( Objects.requireNonNull( value, "Project name is required" ) );
    }
}
