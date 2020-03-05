package com.enonic.xp.core.impl.project;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectPermissionsLevel;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.auth.AuthenticationInfo;

@Component
public final class ProjectPermissionsContextManagerImpl
    implements ProjectPermissionsContextManager
{
    private static final PrincipalKeys ADMIN_ACCESS = PrincipalKeys.from( RoleKeys.ADMIN, RoleKeys.CONTENT_MANAGER_ADMIN );

    private static final PrincipalKeys MANAGER_ACCESS = PrincipalKeys.from( ADMIN_ACCESS, List.of( RoleKeys.CONTENT_MANAGER_APP ) );

    private RepositoryService repositoryService;

    @Override
    public Context initGetContext( final ProjectName projectName )
    {
        final AuthenticationInfo authenticationInfo = ContextAccessor.current().getAuthInfo();
        if ( hasAdminAccess( authenticationInfo ) || hasAnyProjectPermission( projectName, authenticationInfo ) )
        {
            return adminContext();
        }
        else
        {
            throw new ProjectAccessException( authenticationInfo.getUser(), projectName, "get" );
        }
    }

    @Override
    public Context initListContext()
    {
        final AuthenticationInfo authenticationInfo = ContextAccessor.current().getAuthInfo();
        if ( hasManagerAccess( authenticationInfo ) )
        {
            return adminContext();
        }
        else
        {
            throw new ProjectAccessException( authenticationInfo.getUser(), null, "list" );
        }
    }

    @Override
    public Context initDeleteContext()
    {
        final AuthenticationInfo authenticationInfo = ContextAccessor.current().getAuthInfo();
        if ( hasAdminAccess( authenticationInfo ) )
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
        if ( hasAdminAccess( authenticationInfo ) || hasOwnerProjectPermission( projectName, authenticationInfo ) )
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
        if ( hasAdminAccess( authenticationInfo ) )
        {
            return adminContext();
        }
        else
        {
            throw new ProjectAccessException( authenticationInfo.getUser(), null, "create" );
        }
    }

    @Override
    public boolean hasAdminAccess( final AuthenticationInfo authenticationInfo )
    {
        return ADMIN_ACCESS.stream().anyMatch( authenticationInfo::hasRole );
    }

    @Override
    public boolean hasManagerAccess( final AuthenticationInfo authenticationInfo )
    {
        return MANAGER_ACCESS.stream().anyMatch( authenticationInfo::hasRole );
    }

    @Override
    public boolean hasAnyProjectPermission( final ProjectName projectName, final AuthenticationInfo authenticationInfo )
    {
        return hasPermissions( projectName, authenticationInfo, Set.of( ProjectPermissionsLevel.OWNER, ProjectPermissionsLevel.EXPERT,
                                                                        ProjectPermissionsLevel.CONTRIBUTOR ) );
    }

    private boolean hasOwnerProjectPermission( final ProjectName projectName, final AuthenticationInfo authenticationInfo )
    {
        return hasPermissions( projectName, authenticationInfo, Set.of( ProjectPermissionsLevel.OWNER ) );
    }

    private boolean hasExpertProjectPermission( final ProjectName projectName, final AuthenticationInfo authenticationInfo )
    {
        return hasPermissions( projectName, authenticationInfo, Set.of( ProjectPermissionsLevel.EXPERT ) );
    }

    private boolean hasContributorProjectPermission( final ProjectName projectName, final AuthenticationInfo authenticationInfo )
    {
        return hasPermissions( projectName, authenticationInfo, Set.of( ProjectPermissionsLevel.CONTRIBUTOR ) );
    }


    private boolean hasPermissions( final ProjectName projectName, final AuthenticationInfo authenticationInfo,
                                    final Collection<ProjectPermissionsLevel> permissions )
    {
        if ( projectName == null )
        {
            return false;
        }

        return adminContext().callWith( () -> {

            final Repository repository = this.repositoryService.get( projectName.getRepoId() );

            if ( repository == null )
            {
                throw new ProjectNotFoundException( projectName );
            }

            final Project project = Project.from( repository );
            final PrincipalKeys projectPrincipalKeys = project.getPermissions().getPermissions( permissions );

            final PrincipalKeys userKeys = authenticationInfo.getPrincipals();

            if ( projectPrincipalKeys.stream().anyMatch( userKeys::contains ) )
            {
                return true;
            }

            return false;
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

    @Reference
    public void setRepositoryService( final RepositoryService repositoryService )
    {
        this.repositoryService = repositoryService;
    }
}
