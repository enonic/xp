package com.enonic.xp.admin.impl.rest.resource.content.task;

import com.google.common.base.Strings;

import com.enonic.xp.admin.impl.rest.resource.content.RestoreContentProgressListener;
import com.enonic.xp.admin.impl.rest.resource.content.json.RestoreContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.query.ContentQueryWithChildren;
import com.enonic.xp.archive.RestoreContentException;
import com.enonic.xp.archive.RestoreContentParams;
import com.enonic.xp.archive.RestoreContentsResult;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.task.AbstractRunnableTask;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

public class RestoreRunnableTask
    extends AbstractRunnableTask
{
    private final RestoreContentJson params;

    private RestoreRunnableTask( Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    @Override
    public void run( final TaskId id, final ProgressReporter progressReporter )
    {
        final ContentIds contentToRestoreList = ContentIds.from( params.getContentIds() );
        progressReporter.info( "Restoring content" );

        final RestoreContentProgressListener listener = new RestoreContentProgressListener( progressReporter );

        final long childrenIds = ContentQueryWithChildren.create().
            contentService( this.contentService ).
            contentsPaths( contentService.getByIds( new GetContentByIdsParams( contentToRestoreList ) ).getPaths() ).
            build().
            find().
            getTotalHits();
        final int contentIds = contentToRestoreList.getSize();

        listener.setTotal( Math.toIntExact( childrenIds + contentIds ) );

        RestoreRunnableTaskResult.Builder result = RestoreRunnableTaskResult.create();

        final ContentPath path = Strings.nullToEmpty( params.getPath() ).isBlank() ? null : ContentPath.from( params.getPath() );

        for ( ContentId contentId : contentToRestoreList )
        {
            final RestoreContentParams restoreContentParams = RestoreContentParams.create().
                contentId( contentId ).
                path( path ).
                restoreContentListener( listener ).
                build();
            try
            {
                final RestoreContentsResult restoreResult = contentService.restore( restoreContentParams );
                result.succeeded( restoreResult.getRestoredContents() );
            }
            catch ( RestoreContentException e )
            {
                result.failed( e.getPath() );
            }
        }

        progressReporter.info( result.build().toJson() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractRunnableTask.Builder<Builder>
    {
        private RestoreContentJson params;

        public Builder params( RestoreContentJson params )
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

        @Override
        public RestoreRunnableTask build()
        {
            return new RestoreRunnableTask( this );
        }
    }
}
