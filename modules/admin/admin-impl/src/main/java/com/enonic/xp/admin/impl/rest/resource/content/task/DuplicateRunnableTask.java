package com.enonic.xp.admin.impl.rest.resource.content.task;

import com.enonic.xp.admin.impl.rest.resource.content.DuplicateContentProgressListener;
import com.enonic.xp.admin.impl.rest.resource.content.json.DuplicateContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.query.ContentQueryWithChildren;
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
        int duplicated = 0;
        int failed = 0;
        String contentName = "";
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

                contentName = result.getContentName();
                duplicated++;
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

        final String message = getMessage( duplicated, failed, contentName );
        if ( duplicated > 0 )
        {
            progressReporter.info( message );
        }
        else
        {
            throw new RuntimeException( message );
        }
    }

    private String getMessage( final int duplicated, final int failed, final String contentName )
    {
        final int total = duplicated + failed;
        switch ( total )
        {
            case 0:
                return "The item is already duplicated.";

            case 1:
                if ( duplicated == 1 )
                {
                    return "\"" + contentName + "\" item is duplicated.";
                }
                else
                {
                    return "Content could not be duplicated.";
                }

            default:
                final StringBuilder builder = new StringBuilder();
                if ( duplicated > 0 )
                {
                    builder.append( duplicated ).append( duplicated > 1 ? " items are " : " item is " ).append( "duplicated. " );
                }
                if ( failed > 0 )
                {
                    builder.append( failed ).append( failed > 1 ? " items " : " item " ).append( "failed to be duplicated. " );
                }
                return builder.toString().trim();
        }
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
