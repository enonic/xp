package com.enonic.xp.core.impl.project;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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

    private static final PrincipalKeys MANAGER_ACCESS = PrincipalKeys.from( ADMIN_ACCESS, List.of( RoleKeys.CONTENT_MANAGER_APP ) );

    public static PrincipalKey createRoleKey( final ProjectName projectName, final ProjectRole projectRole )
    {
        return doCreateRoleKey( projectName, projectRole );
    }

    public static PrincipalKeys createRoleKeys( final ProjectName projectName, final Collection<ProjectRole> projectRoles )
    {
        return PrincipalKeys.from( projectRoles.stream().
            map( projectRole -> doCreateRoleKey( projectName, projectRole ) ).
            collect( Collectors.toSet() ) );
    }

    public static boolean hasAdminAccess( final AuthenticationInfo authenticationInfo )
    {
        return ADMIN_ACCESS.stream().anyMatch( authenticationInfo::hasRole );
    }

    public static boolean hasManagerAccess( final AuthenticationInfo authenticationInfo )
    {
        return MANAGER_ACCESS.stream().anyMatch( authenticationInfo::hasRole );
    }

    private static PrincipalKey doCreateRoleKey( final ProjectName projectName, final ProjectRole projectRole )
    {
        final String roleName = ProjectConstants.PROJECT_NAME_PREFIX + projectName + "." + projectRole.name().toLowerCase();
        return PrincipalKey.ofRole( roleName );
    }
}
