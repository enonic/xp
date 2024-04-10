package com.enonic.xp.lib.project.command;

import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.node.ApplyPermissionsMode;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

public final class ApplyProjectReadAccessCommand
    extends AbstractProjectRootCommand
{
    private final boolean isPublic;

    private ApplyProjectReadAccessCommand( final Builder builder )
    {
        super( builder );
        this.isPublic = builder.isPublic;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Boolean execute()
    {
        return projectRepoContext.callWith( this::doExecute );
    }

    private Boolean doExecute()
    {
        final Content contentRoot = this.contentService.getByPath( ContentPath.ROOT );

        final AccessControlList newList = isPublic
            ? doAddEveryonePermissions( contentRoot.getPermissions() )
            : doRemoveEveryonePermissions( contentRoot.getPermissions() );

        contentService.applyPermissions( ApplyContentPermissionsParams.create().
            permissions( newList ).
            contentId( contentRoot.getId() ).applyPermissionsMode( ApplyPermissionsMode.TREE ).
            build() );

        return GetProjectReadAccessCommand.create().
            contentService( this.contentService ).
            projectName( this.projectName ).
            build().
            execute();
    }

    private AccessControlList doRemoveEveryonePermissions( final AccessControlList permissions )
    {
        return AccessControlList.create( permissions ).
            remove( RoleKeys.EVERYONE ).
            build();
    }

    private AccessControlList doAddEveryonePermissions( AccessControlList permissions )
    {
        return AccessControlList.create( permissions ).
            add( AccessControlEntry.create().
                principal( RoleKeys.EVERYONE ).
                allow( Permission.READ ).
                build() ).
            build();
    }

    public static final class Builder
        extends AbstractProjectRootCommand.Builder<Builder>
    {
        private boolean isPublic;

        private Builder()
        {
        }

        public Builder setPublic( final boolean value )
        {
            this.isPublic = value;
            return this;
        }

        public ApplyProjectReadAccessCommand build()
        {
            validate();
            return new ApplyProjectReadAccessCommand( this );
        }

    }
}
