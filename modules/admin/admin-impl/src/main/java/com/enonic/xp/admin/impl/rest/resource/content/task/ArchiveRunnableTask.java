package com.enonic.xp.admin.impl.rest.resource.content.task;

import com.enonic.xp.admin.impl.rest.resource.content.ArchiveContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.ArchiveContentProgressListener;
import com.enonic.xp.admin.impl.rest.resource.content.query.ContentQueryWithChildren;
import com.enonic.xp.archive.ArchiveContentException;
import com.enonic.xp.archive.ArchiveContentParams;
import com.enonic.xp.archive.ArchiveContentsResult;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.task.AbstractRunnableTask;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

public class ArchiveRunnableTask
    extends AbstractRunnableTask
{
    private final ArchiveContentJson params;

    private ArchiveRunnableTask( Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    @Override
    public void run( final TaskId id, final ProgressReporter progressReporter )
    {
        final ContentIds contentToArchiveList = ContentIds.from( params.getContentIds() );
        progressReporter.info( "Archiving content" );

        final ArchiveContentProgressListener listener = new ArchiveContentProgressListener( progressReporter );

        final long childrenIds = ContentQueryWithChildren.create().
            contentService( this.contentService ).
            contentsPaths( contentService.getByIds( new GetContentByIdsParams( contentToArchiveList ) ).getPaths() ).
            build().
            find().
            getTotalHits();
        final int contentIds = contentToArchiveList.getSize();

        listener.setTotal( Math.toIntExact( childrenIds + contentIds ) );

        ArchiveRunnableTaskResult.Builder result = ArchiveRunnableTaskResult.create();

        for ( ContentId contentId : contentToArchiveList )
        {
            final ArchiveContentParams archiveContentParams = ArchiveContentParams.create().
                contentId( contentId ).
                archiveContentListener( listener ).
                build();
            try
            {
                final ArchiveContentsResult archiveResult = contentService.archive( archiveContentParams );
                result.succeeded( archiveResult.getArchivedContents() );
            }
            catch ( ArchiveContentException e )
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
        private ArchiveContentJson params;

        public Builder params( ArchiveContentJson params )
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
        public ArchiveRunnableTask build()
        {
            return new ArchiveRunnableTask( this );
        }
    }
}
