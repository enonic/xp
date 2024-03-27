package com.enonic.xp.core.impl.project;

import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.project.ProjectRole;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

public final class CreateProjectIssuesAccessListCommand
    extends AbstractProjectCommand
{
    private CreateProjectIssuesAccessListCommand( final Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public AccessControlList execute()
    {
        return createIssuesRootPermissions();
    }

    private AccessControlList createIssuesRootPermissions()
    {
        if ( projectName == null )
        {
            return null;
        }

        return AccessControlList.create().
            add( AccessControlEntry.create().
                allowAll().
                principal( RoleKeys.ADMIN ).
                build() ).
            add( AccessControlEntry.create().
                allowAll().
                principal( RoleKeys.CONTENT_MANAGER_ADMIN ).
                build() ).
            add( AccessControlEntry.create().
                allow( Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE ).
                principal( ProjectAccessHelper.createRoleKey( projectName, ProjectRole.OWNER ) ).
                build() ).
            add( AccessControlEntry.create().
                allow( Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE ).
                principal( ProjectAccessHelper.createRoleKey( projectName, ProjectRole.EDITOR ) ).
                build() ).
            add( AccessControlEntry.create().
                allow( Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE ).
                principal( ProjectAccessHelper.createRoleKey( projectName, ProjectRole.AUTHOR ) ).
                build() ).
            add( AccessControlEntry.create().
                allow( Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE ).
                principal( ProjectAccessHelper.createRoleKey( projectName, ProjectRole.CONTRIBUTOR ) ).
                build() ).
            add( AccessControlEntry.create().
                allow( Permission.READ ).
                principal( ProjectAccessHelper.createRoleKey( projectName, ProjectRole.VIEWER ) ).
                build() ).
            build();
    }

    public static final class Builder
        extends AbstractProjectCommand.Builder<Builder>
    {
        private Builder()
        {
        }

        @Override
        void validate()
        {
            super.validate();
        }

        public CreateProjectIssuesAccessListCommand build()
        {
            validate();
            return new CreateProjectIssuesAccessListCommand( this );
        }

    }
}
