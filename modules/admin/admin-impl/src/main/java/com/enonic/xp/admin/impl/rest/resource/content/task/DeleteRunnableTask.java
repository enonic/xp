package com.enonic.xp.admin.impl.rest.resource.content.task;

import java.util.stream.Collectors;

import com.enonic.xp.admin.impl.rest.resource.content.ContentResource;
import com.enonic.xp.admin.impl.rest.resource.content.DeleteContentProgressListener;
import com.enonic.xp.admin.impl.rest.resource.content.json.DeleteContentJson;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.content.DeleteContentsResult;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.task.AbstractRunnableTask;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

public class DeleteRunnableTask
    extends AbstractRunnableTask
{
    private final DeleteContentJson params;

    private DeleteRunnableTask( Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    private ContentPaths filterChildrenIfParentPresents( final ContentPaths sourceContentPaths )
    {
        return ContentPaths.from( sourceContentPaths.stream().
            filter( contentPath -> sourceContentPaths.stream().noneMatch( contentPath::isChildOf ) ).
            collect( Collectors.toList() ) );
    }

    private long countContentsToDelete( final ContentPaths contentsToDeleteList )
    {
        long parentsToDeleteNumber = contentsToDeleteList.getSize();
        long childrenToDeleteNumber = countChildrenToDelete( contentsToDeleteList );

        long result = parentsToDeleteNumber + childrenToDeleteNumber;

        if ( params.isDeleteOnline() )
        {
            final Context masterContext = ContextBuilder.from( ContextAccessor.current() ).
                branch( ContentConstants.BRANCH_MASTER ).
                build();

            final long parentsToDeleteInMaster =
                masterContext.callWith( () -> this.contentService.getByPaths( contentsToDeleteList ).getSize() );
            final long childrenToDeleteInMaster = masterContext.callWith( () -> countChildrenToDelete( contentsToDeleteList ) );

            result += parentsToDeleteInMaster + childrenToDeleteInMaster;
        }

        return result;
    }

    private long countChildrenToDelete( final ContentPaths contentsToDeleteList )
    {
        return contentsToDeleteList.
            stream().
            mapToLong( contentPath -> this.contentService.findIdsByParent( FindContentByParentParams.create().
                parentPath( contentPath ).
                size( ContentResource.GET_ALL_SIZE_FLAG ).
                recursive( true ).
                build() ).
                getTotalHits() ).
            sum();
    }

    @Override
    public void run( final TaskId id, final ProgressReporter progressReporter )
    {
        final ContentPaths contentsToDeleteList = this.filterChildrenIfParentPresents( ContentPaths.from( params.getContentPaths() ) );
        progressReporter.info( "Deleting content" );

        final DeleteContentProgressListener listener = new DeleteContentProgressListener( progressReporter );
        final DeleteRunnableTaskResult.Builder resultBuilder = DeleteRunnableTaskResult.create();

        listener.setTotal( Math.toIntExact( countContentsToDelete( contentsToDeleteList ) ) );

        for ( final ContentPath contentToDelete : contentsToDeleteList )
        {
            final DeleteContentParams deleteContentParams = DeleteContentParams.create().
                contentPath( contentToDelete ).
                deleteOnline( params.isDeleteOnline() ).
                deleteContentListener( listener ).
                build();

            try
            {
                DeleteContentsResult deleteResult = contentService.deleteWithoutFetch( deleteContentParams );

                final ContentIds deletedContents = deleteResult.getDeletedContents();
                if ( deletedContents.getSize() == 1 )
                {
                    resultBuilder.succeeded( contentToDelete );
                }
                else
                {
                    resultBuilder.succeeded( deletedContents );
                }
                final ContentIds pendingContents = deleteResult.getPendingContents();
                if ( pendingContents.getSize() == 1 )
                {
                    resultBuilder.pending( contentToDelete );
                }
                else if ( pendingContents.getSize() > 1 )
                {
                    resultBuilder.pending( pendingContents );
                }
            }
            catch ( final Exception e )
            {
                try
                {
                    Content content = contentService.getByPath( contentToDelete );
                    if ( content != null )
                    {
                        resultBuilder.failed( contentToDelete );
                    }
                }
                catch ( final Exception e2 )
                {
                    resultBuilder.failed( contentToDelete );
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
        private DeleteContentJson params;

        public Builder params( DeleteContentJson params )
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

        public DeleteRunnableTask build()
        {
            return new DeleteRunnableTask( this );
        }
    }
}
