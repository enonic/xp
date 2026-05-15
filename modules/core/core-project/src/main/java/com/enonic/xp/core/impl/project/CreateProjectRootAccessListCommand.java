package com.enonic.xp.core.impl.project;

import com.enonic.xp.project.ProjectRole;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;


public final class CreateProjectRootAccessListCommand
    extends AbstractProjectCommand
{
    private final boolean isPublic;

    private CreateProjectRootAccessListCommand( final Builder builder )
    {
        super( builder );
        this.isPublic = builder.isPublic;
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
        final AccessControlList.Builder builder = AccessControlList.create()
            .add( AccessControlEntry.create().allowAll().principal( RoleKeys.ADMIN ).build() )
            .add( AccessControlEntry.create().allowAll().principal( RoleKeys.CONTENT_MANAGER_ADMIN ).build() )
            .add( AccessControlEntry.create()
                      .allowAll()
                      .principal( ProjectAccessHelper.createRoleKey( projectName, ProjectRole.OWNER ) )
                      .build() )
            .add( AccessControlEntry.create()
                      .allowAll()
                      .principal( ProjectAccessHelper.createRoleKey( projectName, ProjectRole.EDITOR ) )
                      .build() )
            .add( AccessControlEntry.create()
                      .allow( Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE )
                      .principal( ProjectAccessHelper.createRoleKey( projectName, ProjectRole.AUTHOR ) )
                      .build() )
            .add( AccessControlEntry.create()
                      .allow( Permission.READ )
                      .principal( ProjectAccessHelper.createRoleKey( projectName, ProjectRole.CONTRIBUTOR ) )
                      .build() )
            .add( AccessControlEntry.create()
                      .allow( Permission.READ )
                      .principal( ProjectAccessHelper.createRoleKey( projectName, ProjectRole.VIEWER ) )
                      .build() );

        if ( isPublic )
        {
            builder.add( AccessControlEntry.create().allow( Permission.READ ).principal( RoleKeys.EVERYONE ).build() );
        }

        return builder.build();
    }

    public static final class Builder
        extends AbstractProjectCommand.Builder<Builder>
    {
        boolean isPublic;

        private Builder()
        {
        }

        public Builder isPublic( final boolean isPublic )
        {
            this.isPublic = isPublic;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
        }

        public CreateProjectRootAccessListCommand build()
        {
            validate();
            return new CreateProjectRootAccessListCommand( this );
        }
    }
}
