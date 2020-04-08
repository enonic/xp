package com.enonic.xp.core.impl.project;

import java.text.MessageFormat;

import com.enonic.xp.exception.NotFoundException;
import com.enonic.xp.project.ProjectName;

public final class ProjectNotFoundException
    extends NotFoundException
{
    public ProjectNotFoundException( final ProjectName projectName )
    {
        super( MessageFormat.format( "Project [{0}] was not found", projectName.toString() ) );
    }
}
