package com.enonic.xp.core.impl.project;

import java.util.Set;

import com.enonic.xp.context.Context;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectRole;
import com.enonic.xp.security.auth.AuthenticationInfo;

public interface ProjectPermissionsContextManager
{
    Context initCreateContext();

    Context initDeleteContext( final ProjectName projectName );

    Context initListContext();

    Context initGetContext( final ProjectName projectName );

    Context initUpdateContext( final ProjectName projectName );

    boolean hasAnyProjectRole( final AuthenticationInfo authenticationInfo, final ProjectName projectName,
                               final Set<ProjectRole> projectRoles );
}
