package com.enonic.xp.admin.impl.rest.resource.content.task;

import java.util.stream.Collectors;

import com.enonic.xp.admin.impl.rest.resource.content.DeleteContentProgressListener;
import com.enonic.xp.admin.impl.rest.resource.content.json.DeleteContentJson;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.content.DeleteContentsResult;
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

    @Override
    public void run( final TaskId id, final ProgressReporter progressReporter )
    {
        final ContentPaths contentsToDeleteList = this.filterChildrenIfParentPresents( ContentPaths.from( params.getContentPaths() ) );
        progressReporter.info( "Deleting content" );

        final DeleteContentProgressListener listener = new DeleteContentProgressListener( progressReporter );
        final DeleteRunnableTaskResult.Builder resultBuilder = DeleteRunnableTaskResult.create();
        for ( final ContentPath contentToDelete : contentsToDeleteList )
        {
            final DeleteContentParams deleteContentParams = DeleteContentParams.create().
                contentPath( contentToDelete ).
                deleteOnline( params.isDeleteOnline() ).
                build();

            try
            {
                DeleteContentsResult deleteResult = contentService.deleteWithoutFetch( deleteContentParams );

                if ( deleteResult.getDeletedContents().getSize() > 0 )
                {
                    resultBuilder.deleted( contentToDelete );
                }
                if ( deleteResult.getPendingContents().getSize() > 0 )
                {
                    resultBuilder.pending( contentToDelete );
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

            listener.contentDeleted( 1 );
        }

        progressReporter.info( resultBuilder.build().toJson() );
    }

    private String getMessage( final int deleted, final int pending, final int failed )
    {
        final int total = deleted + pending + failed;
        switch ( total )
        {
            case 0:
                return "Nothing to delete.";

            case 1:
                if ( deleted == 1 )
                {
                    return "The item is deleted.";
                }
                else if ( pending == 1 )
                {
                    return "The item is marked for deletion.";
                }
                else
                {
                    return "Content could not be deleted.";
                }

            default:
                final StringBuilder builder = new StringBuilder();
                if ( deleted > 0 )
                {
                    builder.append( deleted ).append( deleted > 1 ? " items are " : " item is " ).append( "deleted. " );
                }
                if ( pending > 0 )
                {
                    builder.append( pending ).append( pending > 1 ? " items are " : " item is " ).append( "marked for deletion. " );
                }
                if ( failed > 0 )
                {
                    builder.append( failed ).append( failed > 1 ? " items " : " item " ).append( "failed to be deleted. " );
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
