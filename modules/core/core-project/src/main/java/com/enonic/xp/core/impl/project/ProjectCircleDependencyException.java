package com.enonic.xp.core.impl.project;

import java.text.MessageFormat;
import java.util.Collection;

import com.enonic.xp.exception.BaseException;
import com.enonic.xp.project.ProjectName;

public final class ProjectCircleDependencyException
    extends BaseException
{

    public ProjectCircleDependencyException( final ProjectName project, final Collection<ProjectName> parentProjects )
    {
        super( MessageFormat.format( "Project with name [{0}] and parent projects [{1}] creates a circle dependency", project,
                                     parentProjects ) );
    }
}
