package com.enonic.xp.core.impl.project;

import java.util.EnumSet;
import java.util.Set;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectRole;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.auth.AuthenticationInfo;

@Component
public final class ProjectPermissionsContextManagerImpl
    implements ProjectPermissionsContextManager
{
    @Override
    public Context initGetContext( final ProjectName projectName )
    {
        final AuthenticationInfo authenticationInfo = ContextAccessor.current().getAuthInfo();

        if ( ProjectConstants.DEFAULT_PROJECT_NAME.equals( projectName ) )
        {
            if ( ProjectAccessHelper.hasManagerAccess( authenticationInfo ) )
            {
                return adminContext();
            }
        }
        else if ( ProjectAccessHelper.hasAdminAccess( authenticationInfo ) ||
            hasAnyProjectRole( authenticationInfo, projectName, EnumSet.allOf( ProjectRole.class ) ) )
        {
            return adminContext();
        }

        throw new ProjectAccessException( authenticationInfo.getUser(), projectName, "get" );
    }

    @Override
    public Context initListContext()
    {
        return adminContext();
    }

    @Override
    public Context initDeleteContext( final ProjectName projectName )
    {
        final AuthenticationInfo authenticationInfo = ContextAccessor.current().getAuthInfo();
        if ( ProjectAccessHelper.hasAdminAccess( authenticationInfo ) && !ProjectConstants.DEFAULT_PROJECT_NAME.equals( projectName ) )
        {
            return adminContext();
        }
        else
        {
            throw new ProjectAccessException( authenticationInfo.getUser(), null, "delete" );
        }
    }

    @Override
    public Context initUpdateContext( final ProjectName projectName )
    {
        final AuthenticationInfo authenticationInfo = ContextAccessor.current().getAuthInfo();
        if ( ProjectAccessHelper.hasAdminAccess( authenticationInfo ) || ( !ProjectConstants.DEFAULT_PROJECT_NAME.equals( projectName ) &&
            hasOwnerProjectPermission( projectName, authenticationInfo ) ) )
        {
            return adminContext();
        }
        else
        {
            throw new ProjectAccessException( authenticationInfo.getUser(), projectName, "update" );
        }
    }

    @Override
    public Context initCreateContext()
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

    @Override
    public boolean hasAnyProjectRole( final AuthenticationInfo authenticationInfo, final ProjectName projectName,
                                      final Set<ProjectRole> projectRoles )
    {
        return hasPermissions( projectName, authenticationInfo, projectRoles );
    }

    private boolean hasOwnerProjectPermission( final ProjectName projectName, final AuthenticationInfo authenticationInfo )
    {
        return hasPermissions( projectName, authenticationInfo, Set.of( ProjectRole.OWNER ) );
    }

    private boolean hasPermissions( final ProjectName projectName, final AuthenticationInfo authenticationInfo,
                                    final Set<ProjectRole> projectRoles )
    {
        if ( projectName == null )
        {
            return false;
        }

        return adminContext().callWith( () -> {
            final PrincipalKeys rolesKeys = ProjectAccessHelper.createRoleKeys( projectName, projectRoles );
            return rolesKeys.stream().anyMatch( authenticationInfo::hasRole );
        } );
    }

    private Context adminContext()
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
