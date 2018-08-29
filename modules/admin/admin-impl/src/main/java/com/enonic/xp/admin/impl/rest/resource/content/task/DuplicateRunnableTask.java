package com.enonic.xp.admin.impl.rest.resource.content.task;

import java.util.stream.Collectors;

import com.enonic.xp.admin.impl.rest.resource.content.DuplicateContentProgressListener;
import com.enonic.xp.admin.impl.rest.resource.content.json.DuplicateContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.DuplicateContentsJson;
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

    private final DuplicateContentsJson params;

    private DuplicateRunnableTask( Builder builder )
    {
        super( builder );
        this.authInfo = builder.authInfo;
        this.params = builder.params;
    }

    @Override
    public void run( final TaskId id, final ProgressReporter progressReporter )
    {
        final ContentIds contentToDuplicateWithChildrenList = ContentIds.from(
            params.getContents().stream().filter( DuplicateContentJson::getIncludeChildren ).map(
                DuplicateContentJson::getContentId ).collect( Collectors.toList() ) );

        progressReporter.info( "Duplicating content" );

        final DuplicateContentProgressListener listener = new DuplicateContentProgressListener( progressReporter );

        final int parentIdsCount = params.getContents().size();

        final int childIdsCount = contentToDuplicateWithChildrenList.stream().map( parentId -> ContentQueryWithChildren.create().
            contentService( this.contentService ).
            contentsIds( ContentIds.from( parentId ) ).
            build().
            find().
            getTotalHits() ).mapToInt( Long::intValue ).sum();

        listener.setTotal( parentIdsCount + childIdsCount );

        final DuplicateRunnableTaskResult.Builder resultBuilder = DuplicateRunnableTaskResult.create();
        for ( DuplicateContentJson content : params.getContents() )
        {

            final ContentId contentId = ContentId.from( content.getContentId() );

            final DuplicateContentParams duplicateContentParams = DuplicateContentParams.create().
                contentId( contentId ).
                creator( authInfo.getUser().getKey() ).
                duplicateContentListener( listener ).
                includeChildren( content.getIncludeChildren() ).
                build();
            try
            {
                final DuplicateContentsResult result = contentService.duplicate( duplicateContentParams );
                if ( result.getDuplicatedContents().getSize() == 1 )
                {
                    resultBuilder.succeeded( result.getSourceContentPath() );
                }
                else
                {
                    resultBuilder.succeeded( result.getDuplicatedContents() );
                }
            }
            catch ( ContentAlreadyMovedException e )
            {
                resultBuilder.alreadyDuplicated( e.getPath() );
            }
            catch ( final Exception e )
            {
                try
                {
                    final Content item = contentService.getById( contentId );
                    resultBuilder.failed( item.getPath() );
                }
                catch ( Exception exc )
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

        private DuplicateContentsJson params;

        public Builder authInfo( AuthenticationInfo authInfo )
        {
            this.authInfo = authInfo;
            return this;
        }

        public Builder params( DuplicateContentsJson params )
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
