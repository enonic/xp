package com.enonic.xp.core.impl.project;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectRole;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.PrincipalRelationship;
import com.enonic.xp.security.PrincipalRelationships;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.auth.AuthenticationInfo;

@Component
public final class ProjectPermissionsContextManagerImpl
    implements ProjectPermissionsContextManager
{
    private static final PrincipalKeys ADMIN_ACCESS = PrincipalKeys.from( RoleKeys.ADMIN, RoleKeys.CONTENT_MANAGER_ADMIN );

    private static final PrincipalKeys MANAGER_ACCESS = PrincipalKeys.from( ADMIN_ACCESS, List.of( RoleKeys.CONTENT_MANAGER_APP ) );

    private RepositoryService repositoryService;

    private SecurityService securityService;

    @Override
    public Context initGetContext( final ProjectName projectName )
    {
        final AuthenticationInfo authenticationInfo = ContextAccessor.current().getAuthInfo();

        if ( ProjectConstants.DEFAULT_PROJECT_NAME.equals( projectName ) )
        {
            if ( hasManagerAccess( authenticationInfo ) )
            {
                return adminContext();
            }
        }
        else if ( hasAdminAccess( authenticationInfo ) || hasAnyProjectPermission( projectName, authenticationInfo ) )
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
        if ( hasAdminAccess( authenticationInfo ) && !ProjectConstants.DEFAULT_PROJECT_NAME.equals( projectName ) )
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
        if ( hasAdminAccess( authenticationInfo ) || ( !ProjectConstants.DEFAULT_PROJECT_NAME.equals( projectName ) &&
            hasAdminProjectPermission( projectName, authenticationInfo ) ) )
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
        return hasPermissions( projectName, authenticationInfo,
                               Set.of( ProjectRole.OWNER, ProjectRole.EDITOR, ProjectRole.AUTHOR, ProjectRole.CONTRIBUTOR,
                                       ProjectRole.VIEWER ) );
    }

    private boolean hasAdminProjectPermission( final ProjectName projectName, final AuthenticationInfo authenticationInfo )
    {
        return hasPermissions( projectName, authenticationInfo, Set.of( ProjectRole.OWNER ) );
    }

    private boolean hasEditorProjectPermission( final ProjectName projectName, final AuthenticationInfo authenticationInfo )
    {
        return hasPermissions( projectName, authenticationInfo, Set.of( ProjectRole.EDITOR ) );
    }

    private boolean hasAuthorProjectPermission( final ProjectName projectName, final AuthenticationInfo authenticationInfo )
    {
        return hasPermissions( projectName, authenticationInfo, Set.of( ProjectRole.AUTHOR ) );
    }

    private boolean hasContributorProjectPermission( final ProjectName projectName, final AuthenticationInfo authenticationInfo )
    {
        return hasPermissions( projectName, authenticationInfo, Set.of( ProjectRole.CONTRIBUTOR ) );
    }


    private boolean hasPermissions( final ProjectName projectName, final AuthenticationInfo authenticationInfo,
                                    final Collection<ProjectRole> projectRoles )
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

            final Set<PrincipalKey> projectPrincipalKeys = getProjectPermissionMembers( projectName, projectRoles );
            final PrincipalKeys userKeys = authenticationInfo.getPrincipals();

            return projectPrincipalKeys.stream().anyMatch( userKeys::contains );
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

    private Set<PrincipalKey> getProjectPermissionMembers( final ProjectName projectName, final Collection<ProjectRole> projectRoles )
    {
        return projectRoles.stream().
            map( projectRole -> projectRole.getRoleKey( projectName ) ).
            map( securityService::getRelationships ).
            flatMap( PrincipalRelationships::stream ).
            map( PrincipalRelationship::getTo ).
            collect( Collectors.toSet() );
    }

    @Reference
    public void setRepositoryService( final RepositoryService repositoryService )
    {
        this.repositoryService = repositoryService;
    }

    @Reference
    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }
}
