package com.enonic.xp.project;

import java.util.Set;

import com.enonic.xp.security.auth.AuthenticationInfo;

public interface ProjectAccessVerifier
{
    boolean hasAnyProjectRole( AuthenticationInfo authenticationInfo, ProjectName projectName, Set<ProjectRole> projectRoles );
}
