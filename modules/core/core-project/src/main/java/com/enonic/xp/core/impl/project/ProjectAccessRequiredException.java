package com.enonic.xp.core.impl.project;

import java.util.Arrays;

import com.enonic.xp.exception.BaseException;
import com.enonic.xp.project.ProjectRole;
import com.enonic.xp.security.PrincipalKey;

public class ProjectAccessRequiredException
    extends BaseException
{
    public ProjectAccessRequiredException( final PrincipalKey user )
    {
        super( "User [{0}] is required to have an admin access", user );
    }

    public ProjectAccessRequiredException( final PrincipalKey user, ProjectRole... projectRoles )
    {
        super( "User [{0}] is required to be a [{1}] or have an admin access", user, Arrays.toString( projectRoles ) );
    }
}
