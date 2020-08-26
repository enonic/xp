package com.enonic.xp.core.impl.project;

import java.text.MessageFormat;

import com.enonic.xp.exception.BaseException;
import com.enonic.xp.project.ProjectName;

public final class ProjectAlreadyExistsException
    extends BaseException
{
    private final ProjectName projectName;

    public ProjectAlreadyExistsException( final ProjectName projectName )
    {
        super( MessageFormat.format( "Project with name [{0}] already exists", projectName ) );
        this.projectName = projectName;
    }

    public ProjectName getProjectName()
    {
        return projectName;
    }
}
