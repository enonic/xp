package com.enonic.xp.admin.impl.rest.resource.content.task;

import com.enonic.xp.admin.impl.rest.resource.content.MoveContentProgressListener;
import com.enonic.xp.admin.impl.rest.resource.content.json.MoveContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.query.ContentQueryWithChildren;
import com.enonic.xp.content.ContentAlreadyMovedException;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.MoveContentsResult;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

public class MoveRunnableTask
    extends CommonRunnableTask
{
    private MoveContentJson params;

    MoveRunnableTask( Builder builder )
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
        int moved = 0;
        int failed = 0;
        String contentName = "";
        for ( ContentId contentId : contentToMoveList )
        {
            final MoveContentParams moveContentParams = MoveContentParams.create().
                contentId( contentId ).
                parentContentPath( params.getParentContentPath() ).
                moveContentListener( listener ).
                build();
            try
            {
                final MoveContentsResult result = contentService.move( moveContentParams );

                contentName = result.getContentName();
                moved++;
            }
            catch ( ContentAlreadyMovedException e )
            {
                continue;
            }
            catch ( final Exception e )
            {
                failed++;
            }
        }

        progressReporter.info( getMessage( moved, failed, contentName ) );
    }

    private String getMessage( final int moved, final int failed, final String contentName )
    {
        final int total = moved + failed;
        switch ( total )
        {
            case 0:
                return "The item is already moved.";

            case 1:
                if ( moved == 1 )
                {
                    return "\"" + contentName + "\" item is moved.";
                }
                else
                {
                    return "Content could not be moved.";
                }

            default:
                final StringBuilder builder = new StringBuilder();
                if ( moved > 0 )
                {
                    builder.append( moved ).append( moved > 1 ? " items are " : " item is " ).append( "moved. " );
                }
                if ( failed > 0 )
                {
                    builder.append( failed ).append( failed > 1 ? " items " : " item " ).append( "failed to be moved. " );
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
        private MoveContentJson params;

        public Builder params( MoveContentJson params )
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

        public MoveRunnableTask build()
        {
            return new MoveRunnableTask( this );
        }
    }
}
