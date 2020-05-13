package com.enonic.xp.lib.project.command;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.Permission;

public final class GetProjectReadAccessCommand
    extends AbstractProjectRootCommand
{
    private GetProjectReadAccessCommand( final Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Boolean execute()
    {
        return doGetProjectReadAccess();
    }

    private Boolean doGetProjectReadAccess()
    {
        final Content content = projectRepoContext.callWith( () -> this.contentService.getByPath( ContentPath.ROOT ) );

        return content.getPermissions().isAllowedFor( RoleKeys.EVERYONE, Permission.READ );
    }

    public static final class Builder
        extends AbstractProjectRootCommand.Builder<Builder>
    {
        private Builder()
        {
        }

        void validate()
        {
            super.validate();
        }

        public GetProjectReadAccessCommand build()
        {
            validate();
            return new GetProjectReadAccessCommand( this );
        }
    }
}
