package com.enonic.xp.admin.impl.rest.resource.content.task;

import com.enonic.xp.admin.impl.rest.resource.content.ApplyPermissionsProgressListener;
import com.enonic.xp.admin.impl.rest.resource.content.ContentResource;
import com.enonic.xp.admin.impl.rest.resource.content.json.ApplyContentPermissionsJson;
import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.ApplyContentPermissionsResult;
import com.enonic.xp.content.ApplyPermissionsListener;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentIdsByParentResult;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.task.AbstractRunnableTask;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

public class ApplyPermissionsRunnableTask
    extends AbstractRunnableTask
{
    private final ApplyContentPermissionsJson params;

    private ApplyPermissionsRunnableTask( Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public void run( final TaskId id, final ProgressReporter progressReporter )
    {
        final ApplyPermissionsListener listener = new ApplyPermissionsProgressListener( progressReporter );

        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        final PrincipalKey modifier =
            authInfo != null && authInfo.isAuthenticated() ? authInfo.getUser().getKey() : PrincipalKey.ofAnonymous();

        final FindContentIdsByParentResult children = this.contentService.findIdsByParent(
            FindContentByParentParams.create().size( ContentResource.GET_ALL_SIZE_FLAG ).recursive( true ).parentId(
                params.getUpdateContentParams().getContentId() ).build() );

        listener.setTotal( ( (Long) children.getTotalHits() ).intValue() + 1 );

        final ApplyPermissionsRunnableTaskResult.Builder resultBuilder = ApplyPermissionsRunnableTaskResult.create();

        final UpdateContentParams updatePermissionsParams = params.getUpdateContentParams().modifier( modifier );
        final Content updatedContent = contentService.update( updatePermissionsParams );

        resultBuilder.succeeded( updatedContent.getPath() );
        listener.permissionsApplied( 1 );

        final ApplyContentPermissionsResult result = contentService.applyPermissions( ApplyContentPermissionsParams.create().
            contentId( updatedContent.getId() ).
            overwriteChildPermissions( params.isOverwriteChildPermissions() ).
            applyContentPermissionsListener( listener ).
            build() );

        resultBuilder.succeeded( result.getSucceedContents() );
        resultBuilder.failed( result.getSkippedContents() );

        progressReporter.info( resultBuilder.build().toJson() );
    }

    public static class Builder
        extends AbstractRunnableTask.Builder
    {
        private ApplyContentPermissionsJson params;

        public Builder params( ApplyContentPermissionsJson params )
        {
            this.params = params;
            return this;
        }

        @Override
        public Builder description( String description )
        {
            super.description( description );
            return this;
        }

        @Override
        public Builder taskService( TaskService taskService )
        {
            super.taskService( taskService );
            return this;
        }

        @Override
        public Builder contentService( ContentService contentService )
        {
            super.contentService( contentService );
            return this;
        }

        public ApplyPermissionsRunnableTask build()
        {
            return new ApplyPermissionsRunnableTask( this );
        }
    }
}
