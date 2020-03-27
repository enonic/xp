package com.enonic.xp.admin.impl.rest.resource.project;

import com.google.common.base.Preconditions;

import com.enonic.xp.admin.impl.rest.resource.content.task.ApplyPermissionsRunnableTask;
import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.project.ProjectReadAccessType;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.task.TaskResultJson;
import com.enonic.xp.task.TaskService;

public final class ApplyProjectReadAccessPermissionsCommand
    extends AbstractProjectReadAccessCommand
{
    private final ProjectReadAccess readAccess;

    private final TaskService taskService;

    private ApplyProjectReadAccessPermissionsCommand( final Builder builder )
    {
        super( builder );
        this.readAccess = builder.readAccess;
        this.taskService = builder.taskService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public TaskResultJson execute()
    {
        return projectRepoContext.callWith( this::doExecute );
    }

    private TaskResultJson doExecute()
    {
        final Content contentRoot = this.contentService.getByPath( ContentPath.ROOT );

        final AccessControlList newList =
            ProjectReadAccessType.PUBLIC.equals( readAccess.getType() )
                ? doAddEveryonePermissions( contentRoot.getId(), contentRoot.getPermissions() )
                : doRemoveEveryonePermissions( contentRoot.getId(), contentRoot.getPermissions() );

        if ( !contentRoot.getPermissions().equals( newList ) )
        {
            return ApplyPermissionsRunnableTask.create().
                params( ApplyContentPermissionsParams.create().
                    permissions( newList ).
                    contentId( contentRoot.getId() ).
                    overwriteChildPermissions( true ).
                    build() ).
                description( "Apply project's content root permissions" ).
                taskService( taskService ).
                contentService( contentService ).
                build().
                createTaskResult();
        }

        return null;
    }

    private AccessControlList doRemoveEveryonePermissions( final ContentId contentId, final AccessControlList permissions )
    {
        return permissions.getEntry( RoleKeys.EVERYONE ) != null && permissions.getEntry( RoleKeys.EVERYONE ).
            isAllowed( Permission.READ ) ?

            AccessControlList.create( permissions ).
                remove( RoleKeys.EVERYONE ).
                build() : permissions;

    }

    private AccessControlList doAddEveryonePermissions( final ContentId contentId, final AccessControlList permissions )
    {
        return permissions.getEntry( RoleKeys.EVERYONE ) == null || !permissions.getEntry( RoleKeys.EVERYONE ).
            isAllowed( Permission.READ ) ? AccessControlList.create( permissions ).
            add( AccessControlEntry.create().
                principal( RoleKeys.EVERYONE ).
                allow( Permission.READ ).
                build() ).
            build() : permissions;
    }

    public static final class Builder
        extends AbstractProjectReadAccessCommand.Builder<Builder>
    {
        private ProjectReadAccess readAccess;

        private TaskService taskService;

        private Builder()
        {
        }

        public Builder readAccess( final ProjectReadAccess readAccess )
        {
            this.readAccess = readAccess;
            return this;
        }

        public Builder taskService( final TaskService taskService )
        {
            this.taskService = taskService;
            return this;
        }

        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.readAccess, "readAccess is required" );
            Preconditions.checkNotNull( this.taskService, "taskService is required" );
        }

        public ApplyProjectReadAccessPermissionsCommand build()
        {
            validate();
            return new ApplyProjectReadAccessPermissionsCommand( this );
        }

    }
}
