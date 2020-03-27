package com.enonic.xp.admin.impl.rest.resource.project;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.project.ProjectReadAccessType;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.Permission;

public final class GetProjectReadAccessCommand
    extends AbstractProjectReadAccessCommand
{
    private PrincipalKeys viewerRoleMembers;

    private GetProjectReadAccessCommand( final Builder builder )
    {
        super( builder );
        this.viewerRoleMembers = builder.viewerRoleMembers;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ProjectReadAccess execute()
    {
        return doGetProjectReadAccess();
    }

    private ProjectReadAccess doGetProjectReadAccess()
    {
        final ProjectReadAccess.Builder readAccess = ProjectReadAccess.create();

        final Content content = projectRepoContext.callWith( () -> this.contentService.getByPath( ContentPath.ROOT ) );

        final boolean hasEveryoneReadPermission = content.getPermissions().isAllowedFor( RoleKeys.EVERYONE, Permission.READ );

        if ( hasEveryoneReadPermission )
        {
            readAccess.setType( ProjectReadAccessType.PUBLIC );
        }
        else
        {
            if ( viewerRoleMembers.isEmpty() )
            {
                readAccess.setType( ProjectReadAccessType.PRIVATE );
            }
            else
            {
                readAccess.setType( ProjectReadAccessType.CUSTOM );
                readAccess.addPrincipals( viewerRoleMembers.getSet() );
            }
        }
        return readAccess.build();
    }

    public static final class Builder
        extends AbstractProjectReadAccessCommand.Builder<Builder>
    {
        private PrincipalKeys viewerRoleMembers;

        private Builder()
        {
        }

        public Builder viewerRoleMembers( final PrincipalKeys viewerRoleMembers )
        {
            this.viewerRoleMembers = viewerRoleMembers;
            return this;
        }

        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.viewerRoleMembers, "viewerRoleMembers is required" );
        }

        public GetProjectReadAccessCommand build()
        {
            validate();
            return new GetProjectReadAccessCommand( this );
        }
    }
}
