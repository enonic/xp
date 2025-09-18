package com.enonic.xp.core.impl.project;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectRole;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class ProjectPermissionsContextManager
{
    public static Context initGetContext( final ProjectName projectName )
    {
        final AuthenticationInfo authenticationInfo = ContextAccessor.current().getAuthInfo();
        if ( ProjectAccessHelper.hasAccess( authenticationInfo, projectName, ProjectRole.values() ) )
        {
            return adminContext();
        }

        throw new ProjectAccessException( authenticationInfo.getUser(), projectName, "get" );
    }

    public static Context initListContext()
    {
        return adminContext();
    }

    public static Context initDeleteContext( final ProjectName projectName )
    {
        final AuthenticationInfo authenticationInfo = ContextAccessor.current().getAuthInfo();
        if ( ProjectAccessHelper.hasAdminAccess( authenticationInfo ) )
        {
            return adminContext();
        }
        else
        {
            throw new ProjectAccessException( authenticationInfo.getUser(), projectName, "delete" );
        }
    }

    public static Context initUpdateContext( final ProjectName projectName )
    {
        final AuthenticationInfo authenticationInfo = ContextAccessor.current().getAuthInfo();
        if ( ProjectAccessHelper.hasAccess( authenticationInfo, projectName, ProjectRole.OWNER ) )
        {
            return adminContext();
        }
        else
        {
            throw new ProjectAccessException( authenticationInfo.getUser(), projectName, "update" );
        }
    }

    public static Context initCreateContext()
    {
        final AuthenticationInfo authenticationInfo = ContextAccessor.current().getAuthInfo();
        if ( ProjectAccessHelper.hasAdminAccess( authenticationInfo ) )
        {
            return adminContext();
        }
        else
        {
            throw new ProjectAccessException( authenticationInfo.getUser(), null, "create" );
        }
    }

    private static Context adminContext()
    {
        final AuthenticationInfo authenticationInfo = ContextAccessor.current().getAuthInfo();

        return authenticationInfo.hasRole( RoleKeys.ADMIN ) ? ContextAccessor.current() : ContextBuilder.create().
            repositoryId( SystemConstants.SYSTEM_REPO_ID ).
            branch( ContentConstants.BRANCH_MASTER ).
            authInfo( AuthenticationInfo.copyOf( authenticationInfo ).
            principals( RoleKeys.ADMIN ).
            build() ).
            build();
    }
}
