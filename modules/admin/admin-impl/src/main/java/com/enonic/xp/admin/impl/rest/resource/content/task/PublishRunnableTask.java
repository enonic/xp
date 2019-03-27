package com.enonic.xp.admin.impl.rest.resource.content.task;

import com.enonic.xp.admin.impl.rest.resource.content.DeleteContentProgressListener;
import com.enonic.xp.admin.impl.rest.resource.content.PublishContentProgressListener;
import com.enonic.xp.admin.impl.rest.resource.content.json.PublishContentJson;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.PublishContentResult;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.task.AbstractRunnableTask;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

public class PublishRunnableTask
    extends AbstractRunnableTask
{
    private final PublishContentJson params;

    private PublishRunnableTask( Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    @Override
    public void run( final TaskId id, final ProgressReporter progressReporter )
    {
        final ContentIds contentIds = ContentIds.from( params.getIds() );
        final ContentIds excludeContentIds = ContentIds.from( params.getExcludedIds() );
        final ContentIds excludeChildrenIds = ContentIds.from( params.getExcludeChildrenIds() );
        final ContentPublishInfo contentPublishInfo = params.getSchedule() == null ? null : ContentPublishInfo.create().
            from( params.getSchedule().getPublishFrom() ).
            to( params.getSchedule().getPublishTo() ).
            build();
        progressReporter.info( "Publishing content" );

        PublishRunnableTaskResult.Builder resultBuilder = PublishRunnableTaskResult.create();

        try
        {
            final PublishContentResult result = contentService.publish( PushContentParams.create().
                target( ContentConstants.BRANCH_MASTER ).
                contentIds( contentIds ).
                excludedContentIds( excludeContentIds ).
                excludeChildrenIds( excludeChildrenIds ).
                contentPublishInfo( contentPublishInfo ).
                includeDependencies( true ).
                pushListener( new PublishContentProgressListener( progressReporter ) ).
                deleteContentListener( new DeleteContentProgressListener( progressReporter ) ).
                build() );

            ContentIds pushed = result.getPushedContents();
            ContentIds deleted = result.getDeletedContents();
            ContentIds failed = result.getFailedContents();
            if ( pushed.getSize() == 1 )
            {
                resultBuilder.succeeded( contentService.getById( pushed.first() ).getPath() );
            }
            else
            {
                resultBuilder.succeeded( pushed );
            }
            if ( failed.getSize() == 1 )
            {
                resultBuilder.failed( contentService.getById( failed.first() ).getPath() );
            }
            else
            {
                resultBuilder.failed( failed );
            }
            if ( deleted.getSize() == 1 )
            {
                resultBuilder.deleted( result.getDeletedPath() );
            }
            else
            {
                resultBuilder.deleted( deleted );
            }
        }
        catch ( final Exception e )
        {
            resultBuilder.failed( contentIds );
        }

        progressReporter.info( resultBuilder.build().toJson() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractRunnableTask.Builder
    {
        private PublishContentJson params;

        public Builder params( PublishContentJson params )
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

        public PublishRunnableTask build()
        {
            return new PublishRunnableTask( this );
        }
    }
}
