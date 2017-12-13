package com.enonic.xp.admin.impl.rest.resource.content.task;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.enonic.xp.admin.impl.rest.resource.content.UnpublishContentProgressListener;
import com.enonic.xp.admin.impl.rest.resource.content.json.UnpublishContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.query.ContentQueryWithChildren;
import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.CompareContentsParams;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.PushContentListener;
import com.enonic.xp.content.UnpublishContentParams;
import com.enonic.xp.content.UnpublishContentsResult;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

public class UnpublishRunnableTask
    extends CommonRunnableTask
{
    private UnpublishContentJson params;

    UnpublishRunnableTask( Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    private ContentIds filterIdsByStatus( final ContentIds ids )
    {
        final List<CompareStatus> statuses = Arrays.asList( CompareStatus.EQUAL, CompareStatus.PENDING_DELETE, CompareStatus.NEWER );
        final CompareContentResults compareResults =
            contentService.compare( new CompareContentsParams( ids, ContentConstants.BRANCH_MASTER ) );

        return ContentIds.from( compareResults.getCompareContentResultsMap().entrySet().
            stream().
            filter( entry -> statuses.contains( entry.getValue().getCompareStatus() ) ).
            map( Map.Entry::getKey ).
            collect( Collectors.toSet() ) );
    }

    @Override
    public void run( final TaskId id, final ProgressReporter progressReporter )
    {
        final ContentIds contentIds = ContentIds.from( params.getIds() );
        progressReporter.info( "Unpublishing content" );

        final PushContentListener listener = new UnpublishContentProgressListener( progressReporter );

        final ContentIds childrenIds = ContentQueryWithChildren.create().
            contentService( this.contentService ).
            contentsIds( contentIds ).
            size( -1 ).
            build().
            find().
            getContentIds();

        final ContentIds filteredChildrenIds = this.filterIdsByStatus( childrenIds );

        listener.contentResolved( filteredChildrenIds.getSize() + contentIds.getSize() );

        final UnpublishContentsResult result = this.contentService.unpublishContent( UnpublishContentParams.create().
            unpublishBranch( ContentConstants.BRANCH_MASTER ).
            contentIds( contentIds ).
            includeChildren( params.isIncludeChildren() ).
            pushListener( listener ).
            build() );

        final ContentIds unpublishedContents = result.getUnpublishedContents();

        String contentName = "";
        if ( unpublishedContents.getSize() == 1 )
        {
            contentName = result.getContentName();
        }

        progressReporter.info( getMessage( unpublishedContents.getSize(), contentName ) );
    }

    private String getMessage( final int unpublished, final String contentName )
    {
        switch ( unpublished )
        {
            case 0:
                return "Nothing to unpublish.";

            case 1:
                return "\"" + contentName + "\" is unpublished.";

            default:
                return unpublished + " items are unpublished.";
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends CommonRunnableTask.Builder
    {
        private UnpublishContentJson params;

        public Builder params( UnpublishContentJson params )
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

        public UnpublishRunnableTask build()
        {
            return new UnpublishRunnableTask( this );
        }
    }
}
