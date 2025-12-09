package com.enonic.xp.core.impl.project;

import java.text.MessageFormat;

import com.enonic.xp.exception.DuplicateElementException;
import com.enonic.xp.project.ProjectName;

public final class ProjectAlreadyExistsException
    extends DuplicateElementException
{
    public ProjectAlreadyExistsException( final ProjectName projectName )
    {
        super( MessageFormat.format( "Project with name [{0}] already exists", projectName ) );
    }
}
