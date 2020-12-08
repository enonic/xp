package com.enonic.xp.core.impl.project;

import java.text.MessageFormat;

import com.enonic.xp.exception.BaseException;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.security.User;

public final class ProjectAccessException
    extends BaseException
{
    private final User user;

    private final ProjectName projectName;

    public ProjectAccessException( final User user, final ProjectName projectName, final String operation )
    {
        super( projectName != null
                   ? MessageFormat.format( "Denied [{0}] user access to [{1}] project for [{2}] operation",
                                           user != null ? user.getKey() : "unknown", projectName, operation )
                   : MessageFormat.format( "Denied [{0}] user access for [{1}] operation",
                                           user != null ? user.getKey().toString() : "unknown", operation ) );
        this.user = user;
        this.projectName = projectName;
    }

    public User getUser()
    {
        return user;
    }

    public ProjectName getProjectName()
    {
        return projectName;
    }
}

