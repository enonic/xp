package com.enonic.xp.core.impl.project;

import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.project.ProjectRole;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

public final class CreateProjectRootPermissionsCommand
    extends AbstractProjectRolesCommand
{
    private CreateProjectRootPermissionsCommand( final Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public AccessControlList execute()
    {
        return createContentRootPermissions();
    }

    private AccessControlList createContentRootPermissions()
    {
        if ( projectName == null || ProjectConstants.DEFAULT_PROJECT_NAME.equals( projectName ) )
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
                allowAll().
                principal( createRoleKey( ProjectRole.OWNER ) ).
                build() ).
            add( AccessControlEntry.create().
                allowAll().
                principal( createRoleKey( ProjectRole.EDITOR ) ).
                build() ).
            add( AccessControlEntry.create().
                allow( Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE ).
                principal( createRoleKey( ProjectRole.AUTHOR ) ).
                build() ).
            add( AccessControlEntry.create().
                allow( Permission.READ ).
                principal( createRoleKey( ProjectRole.CONTRIBUTOR ) ).
                build() ).
            add( AccessControlEntry.create().
                allow( Permission.READ ).
                principal( createRoleKey( ProjectRole.VIEWER ) ).
                build() ).
            build();
    }

    public static final class Builder
        extends AbstractProjectRolesCommand.Builder<Builder>
    {
        private Builder()
        {
        }

        void validate()
        {
            super.validate();
        }

        public CreateProjectRootPermissionsCommand build()
        {
            validate();
            return new CreateProjectRootPermissionsCommand( this );
        }

    }
}
