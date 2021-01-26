package com.enonic.xp.core.impl.project;

import java.util.Set;

import com.enonic.xp.context.Context;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectRole;
import com.enonic.xp.security.auth.AuthenticationInfo;

public interface ProjectPermissionsContextManager
{
    Context initCreateContext();

    Context initDeleteContext( ProjectName projectName );

    Context initListContext();

    Context initGetContext( ProjectName projectName );

    Context initUpdateContext( ProjectName projectName );

    boolean hasAnyProjectRole( AuthenticationInfo authenticationInfo, ProjectName projectName, Set<ProjectRole> projectRoles );
}
