package com.enonic.xp.admin.impl.rest.resource.content.task;

import com.enonic.xp.admin.impl.rest.resource.content.DuplicateContentProgressListener;
import com.enonic.xp.admin.impl.rest.resource.content.json.DuplicateContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.query.ContentQueryWithChildren;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAlreadyMovedException;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.DuplicateContentParams;
import com.enonic.xp.content.DuplicateContentsResult;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

public class DuplicateRunnableTask
    extends AbstractRunnableTask
{
    private final AuthenticationInfo authInfo;

    private final DuplicateContentJson params;

    private DuplicateRunnableTask( Builder builder )
    {
        super( builder );
        this.authInfo = builder.authInfo;
        this.params = builder.params;
    }

    @Override
    public void run( final TaskId id, final ProgressReporter progressReporter )
    {
        final ContentIds contentToDuplicateList = ContentIds.from( params.getContentIds() );
        progressReporter.info( "Duplicating content" );

        final DuplicateContentProgressListener listener = new DuplicateContentProgressListener( progressReporter );

        final long childrenIds = ContentQueryWithChildren.create().
            contentService( this.contentService ).
            contentsIds( contentToDuplicateList ).
            build().
            find().
            getTotalHits();
        final int contentIds = contentToDuplicateList.getSize();

        listener.setTotal( Math.toIntExact( childrenIds + contentIds ) );
        final DuplicateRunnableTaskResult.Builder resultBuilder = DuplicateRunnableTaskResult.create();
        for ( ContentId contentId : contentToDuplicateList )
        {
            final DuplicateContentParams duplicateContentParams = DuplicateContentParams.create().
                contentId( contentId ).
                creator( authInfo.getUser().getKey() ).
                duplicateContentListener( listener ).
                build();
            try
            {
                final DuplicateContentsResult result = contentService.duplicate( duplicateContentParams );
                resultBuilder.succeeded( result.getContentName() );
            }
            catch ( ContentAlreadyMovedException e )
            {
                resultBuilder.alreadyDuplicated( e.getPath() );
            }
            catch ( final Exception e )
            {
                final Content item = contentService.getById( contentId );
                if ( item != null )
                {
                    resultBuilder.failed( item.getPath() );
                }
                else
                {
                    resultBuilder.failed( ContentIds.from( contentId ) );
                }
            }
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
        private AuthenticationInfo authInfo;

        private DuplicateContentJson params;

        public Builder authInfo( AuthenticationInfo authInfo )
        {
            this.authInfo = authInfo;
            return this;
        }

        public Builder params( DuplicateContentJson params )
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

        public DuplicateRunnableTask build()
        {
            return new DuplicateRunnableTask( this );
        }
    }
}
