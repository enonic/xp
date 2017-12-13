package com.enonic.xp.admin.impl.rest.resource.content.task;

import com.enonic.xp.admin.impl.rest.resource.content.PublishContentProgressListener;
import com.enonic.xp.admin.impl.rest.resource.content.json.PublishContentJson;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.PublishContentResult;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

public class PublishRunnableTask
    extends CommonRunnableTask
{
    private PublishContentJson params;

    PublishRunnableTask( Builder builder )
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

        final PublishContentResult result = contentService.publish( PushContentParams.create().
            target( ContentConstants.BRANCH_MASTER ).
            contentIds( contentIds ).
            excludedContentIds( excludeContentIds ).
            excludeChildrenIds( excludeChildrenIds ).
            contentPublishInfo( contentPublishInfo ).
            includeDependencies( true ).
            pushListener( new PublishContentProgressListener( progressReporter ) ).
            build() );

        final ContentIds pushedContents = result.getPushedContents();
        final ContentIds deletedContents = result.getDeletedContents();
        final ContentIds failedContents = result.getFailedContents();

        String contentName = "";
        final int total = pushedContents.getSize() + deletedContents.getSize() + failedContents.getSize();
        if ( total == 1 )
        {
            if ( pushedContents.getSize() == 1 )
            {
                contentName = contentService.getById( pushedContents.first() ).getDisplayName();
            }
            else if ( failedContents.getSize() == 1 )
            {
                contentName = contentService.getById( failedContents.first() ).getDisplayName();
            }
        }

        progressReporter.info( getMessage( pushedContents.getSize(), failedContents.getSize(), deletedContents.getSize(), contentName ) );
    }

    private String getMessage( final int succeeded, final int failed, final int deleted, final String contentName )
    {
        final int total = succeeded + failed + deleted;
        switch ( total )
        {
            case 0:
                return "Nothing to publish.";

            case 1:
                if ( succeeded == 1 )
                {
                    return "\"" + contentName + "\" is published.";
                }

                if ( failed == 1 )
                {
                    return "\"" + contentName + "\" failed to be published.";
                }

                if ( deleted == 1 )
                {
                    return "The item is deleted.";
                }

            default:
                final StringBuilder builder = new StringBuilder();
                if ( succeeded > 0 )
                {
                    builder.append( succeeded ).append( succeeded > 1 ? " items are " : " item is " ).append( "published. " );
                }
                if ( deleted > 0 )
                {
                    builder.append( deleted ).append( deleted > 1 ? " items are " : " item is " ).append( "deleted. " );
                }
                if ( failed > 0 )
                {
                    builder.append( failed ).append( failed > 1 ? " items " : " item " ).append( "failed to be published. " );
                }
                return builder.toString().trim();
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends CommonRunnableTask.Builder
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
