package com.enonic.xp.core.impl.project;

import java.text.MessageFormat;
import java.util.Collection;

import com.enonic.xp.exception.BaseException;
import com.enonic.xp.project.ProjectName;

public class ProjectMultipleParentsException
    extends BaseException
{
    public ProjectMultipleParentsException( final ProjectName project, final Collection<ProjectName> parents )
    {
        super( MessageFormat.format( "Project [{0}] cannot have multiple parents [{1}]", project, parents ) );
    }
}
