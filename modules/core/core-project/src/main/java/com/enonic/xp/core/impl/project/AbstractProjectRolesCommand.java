package com.enonic.xp.core.impl.project;

import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.project.ProjectRole;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.PrincipalRelationship;
import com.enonic.xp.security.PrincipalRelationships;
import com.enonic.xp.security.SecurityService;

abstract class AbstractProjectRolesCommand
    extends AbstractProjectCommand
{
    final SecurityService securityService;

    private final String projectDisplayName;

    AbstractProjectRolesCommand( final Builder builder )
    {
        super( builder );
        this.securityService = builder.securityService;
        this.projectDisplayName = builder.projectDisplayName;
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

    protected String createRoleDisplayName( final ProjectRole projectRole )
    {
        return this.projectDisplayName + " - " + getRoleNamePostfix( projectRole );
    }

    private String getRoleNamePostfix( final ProjectRole projectRole )
    {
        switch ( projectRole )
        {
            case OWNER:
                return "Owner";
            case EDITOR:
                return "Editor";
            case AUTHOR:
                return "Author";
            case CONTRIBUTOR:
                return "Contributor";
            case VIEWER:
                return "Viewer";
            default:
                throw new IllegalArgumentException( "Cannot parse projectRole: " + projectRole );
        }
    }

    public static class Builder<B extends Builder>
        extends AbstractProjectCommand.Builder<B>
    {
        protected String projectDisplayName;

        private SecurityService securityService;

        public B securityService( final SecurityService securityService )
        {
            this.securityService = securityService;
            return (B) this;
        }

        public B projectDisplayName( final String projectDisplayName )
        {
            this.projectDisplayName = projectDisplayName;
            return (B) this;
        }


        @Override
        void validate()
        {
            Preconditions.checkNotNull( securityService, "securityService cannot be null" );
        }
    }

}
