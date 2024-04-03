package com.enonic.xp.core.impl.project;

import java.util.Collection;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectRole;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class ProjectAccessHelper
{
    private static final PrincipalKeys ADMIN_ACCESS = PrincipalKeys.from( RoleKeys.ADMIN, RoleKeys.CONTENT_MANAGER_ADMIN );

    public static PrincipalKey createRoleKey( final ProjectName projectName, final ProjectRole projectRole )
    {
        return doCreateRoleKey( projectName, projectRole );
    }

    public static PrincipalKeys createRoleKeys( final ProjectName projectName, final Collection<ProjectRole> projectRoles )
    {
        return PrincipalKeys.from( projectRoles.stream()
                                       .map( projectRole -> doCreateRoleKey( projectName, projectRole ) )
                                       .collect( ImmutableSet.toImmutableSet() ) );
    }

    public static boolean hasAdminAccess( final AuthenticationInfo authenticationInfo )
    {
        return ADMIN_ACCESS.stream().anyMatch( authenticationInfo::hasRole );
    }

    private static PrincipalKey doCreateRoleKey( final ProjectName projectName, final ProjectRole projectRole )
    {
        final String roleName = ProjectConstants.PROJECT_NAME_PREFIX + projectName + "." + projectRole.name().toLowerCase();
        return PrincipalKey.ofRole( roleName );
    }
}
