package com.enonic.xp.admin.impl.rest.resource.content.task;

import com.enonic.xp.admin.impl.rest.resource.content.MoveContentProgressListener;
import com.enonic.xp.admin.impl.rest.resource.content.json.MoveContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.query.ContentQueryWithChildren;
import com.enonic.xp.content.ContentAccessException;
import com.enonic.xp.content.ContentAlreadyExistsException;
import com.enonic.xp.content.ContentAlreadyMovedException;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.MoveContentException;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.MoveContentsResult;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;

public class MoveRunnableTask
    extends AbstractRunnableTask
{
    private final MoveContentJson params;

    private MoveRunnableTask( Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    @Override
    public void run( final TaskId id, final ProgressReporter progressReporter )
    {
        final ContentIds contentToMoveList = ContentIds.from( params.getContentIds() );
        progressReporter.info( "Moving content" );

        final MoveContentProgressListener listener = new MoveContentProgressListener( progressReporter );

        final long childrenIds = ContentQueryWithChildren.create().
            contentService( this.contentService ).
            contentsIds( contentToMoveList ).
            build().
            find().
            getTotalHits();
        final int contentIds = contentToMoveList.getSize();

        listener.setTotal( Math.toIntExact( childrenIds + contentIds ) );

        MoveRunnableTaskResult.Builder result = MoveRunnableTaskResult.create().destination( params.getParentContentPath() );

        for ( ContentId contentId : contentToMoveList )
        {
            final MoveContentParams moveContentParams = MoveContentParams.create().
                contentId( contentId ).
                parentContentPath( params.getParentContentPath() ).
                moveContentListener( listener ).
                build();
            try
            {
                final MoveContentsResult moveResult = contentService.move( moveContentParams );
                result.succeeded( moveResult.getContentName() );
            }
            catch ( ContentAlreadyMovedException e )
            {
                result.alreadyMoved( e.getPath() );
            }
            catch ( ContentAlreadyExistsException e )
            {
                result.existsFailed( e.getContentPath() );
            }
            catch ( ContentNotFoundException e )
            {
                result.notExistsFailed( e.getPath() );
            }
            catch ( ContentAccessException e )
            {
                result.accessFailed( e.getContentPath() );
            }
            catch ( MoveContentException e )
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
        private MoveContentJson params;

        public Builder params( MoveContentJson params )
        {
            this.params = params;
            return this;
        }

        public MoveRunnableTask build()
        {
            return new MoveRunnableTask( this );
        }
    }
}
