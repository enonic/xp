package com.enonic.xp.core.impl.content;

import java.util.Arrays;

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
        final String roleName = ProjectConstants.PROJECT_NAME_PREFIX + projectName + "." + projectRole.name().toLowerCase();
        return PrincipalKey.ofRole( roleName );
    }

    public static boolean hasAdminAccess( final AuthenticationInfo authenticationInfo )
    {
        return ADMIN_ACCESS.stream().anyMatch( authenticationInfo::hasRole );
    }

    public static boolean hasAnyAccess( final AuthenticationInfo authenticationInfo, final ProjectName projectName )
    {
        return hasAdminAccess( authenticationInfo ) ||
            Arrays.stream( ProjectRole.values() ).anyMatch( r -> hasPermissions( authenticationInfo, projectName, r ) );
    }

    public static boolean hasAccess( AuthenticationInfo authenticationInfo, ProjectName projectName, ProjectRole projectRole )
    {
        return hasAdminAccess( authenticationInfo ) || hasPermissions( authenticationInfo, projectName, projectRole );
    }

    private static boolean hasPermissions( final AuthenticationInfo authenticationInfo, final ProjectName projectName,
                                           ProjectRole projectRole )
    {
        return authenticationInfo.hasRole( createRoleKey( projectName, projectRole ) );
    }
}
