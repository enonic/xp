package com.enonic.xp.core.impl.project;

import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectRole;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.PrincipalRelationship;
import com.enonic.xp.security.PrincipalRelationships;
import com.enonic.xp.security.SecurityService;

abstract class AbstractProjectRolesCommand
{
    final ProjectName projectName;

    final SecurityService securityService;

    AbstractProjectRolesCommand( final Builder builder )
    {
        this.securityService = builder.securityService;
        this.projectName = builder.projectName;
    }

    protected Set<PrincipalRelationship> doGetAddedMembers( final PrincipalRelationships oldRoleMembers, final PrincipalKeys newRoleMembers,
                                                            final PrincipalKey roleKey )
    {
        return newRoleMembers.
            stream().
            filter( newRoleMember -> oldRoleMembers.
                stream().
                noneMatch( oldRoleMember -> oldRoleMember.getTo().equals( newRoleMember ) ) ).
            map( newMember -> PrincipalRelationship.from( roleKey ).to( newMember ) ).
            collect( Collectors.toSet() );
    }

    protected Set<PrincipalRelationship> doGetRemovedMembers( final PrincipalRelationships oldRoleMembers,
                                                              final PrincipalKeys newRoleMembers )
    {
        return oldRoleMembers.
            stream().
            filter( oldRoleMember -> newRoleMembers.
                stream().
                noneMatch( newRoleMember -> oldRoleMember.getTo().equals( newRoleMember ) ) ).
            collect( Collectors.toSet() );
    }

    protected PrincipalKey createRoleKey( final ProjectRole projectRole )
    {
        final String roleName = ProjectConstants.PROJECT_NAME_PREFIX + projectName + "." + projectRole.getValue().toLowerCase();
        return PrincipalKey.ofRole( roleName );
    }

    public static class Builder<B extends Builder>
    {
        SecurityService securityService;

        ProjectName projectName;

        @SuppressWarnings("unchecked")
        public B securityService( final SecurityService securityService )
        {
            this.securityService = securityService;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B projectName( final ProjectName projectName )
        {
            this.projectName = projectName;
            return (B) this;
        }

        void validate()
        {
            Preconditions.checkNotNull( securityService, "securityService cannot be null" );
            Preconditions.checkNotNull( projectName, "Project name cannot be null" );
            Preconditions.checkArgument( !ProjectConstants.DEFAULT_PROJECT_NAME.equals( projectName ), "Default project has no roles" );
        }

    }

}
