package com.enonic.xp.admin.impl.rest.resource.content.task;

import java.util.Set;
import java.util.stream.Collectors;

import com.enonic.xp.admin.impl.rest.resource.content.DuplicateContentProgressListener;
import com.enonic.xp.admin.impl.rest.resource.content.json.DuplicateContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.DuplicateContentsJson;
import com.enonic.xp.admin.impl.rest.resource.content.query.ContentQueryWithChildren;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAlreadyMovedException;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.DuplicateContentParams;
import com.enonic.xp.content.DuplicateContentProcessor;
import com.enonic.xp.content.DuplicateContentsResult;
import com.enonic.xp.node.DuplicateValueResolver;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;

public class DuplicateRunnableTask
    extends AbstractRunnableTask
{
    private final AuthenticationInfo authInfo;

    protected final DuplicateContentsJson params;

    protected DuplicateRunnableTask( Builder builder )
    {
        super( builder );
        this.authInfo = builder.authInfo;
        this.params = builder.params;
    }

    public static Builder<?> create()
    {
        return new Builder();
    }

    protected Set<DuplicateContentJson> getContentsToDuplicateWithChildren()
    {
        return params.getContents().stream().filter( DuplicateContentJson::getIncludeChildren ).collect( Collectors.toSet() );
    }

    protected DuplicateContentProcessor getProcessor( final DuplicateContentJson content )
    {
        return new DuplicateContentProcessor();
    }

    protected DuplicateValueResolver getValueResolver( final DuplicateContentJson content )
    {
        return new DuplicateValueResolver();
    }

    protected ContentPath getParent( final DuplicateContentJson content )
    {
        return null;
    }

    protected ContentPath getDependenciesPath( final ContentId contentId )
    {
        return null;
    }

    protected DuplicateRunnableTaskResult.Builder getTaskResultBuilder()
    {
        return DuplicateRunnableTaskResult.create();
    }

    @Override
    public void run( final TaskId id, final ProgressReporter progressReporter )
    {
        final Set<DuplicateContentJson> contentToDuplicateWithChildrenSet = getContentsToDuplicateWithChildren();

        progressReporter.info( "Duplicating content" );

        final DuplicateContentProgressListener listener = new DuplicateContentProgressListener( progressReporter );

        final int parentIdsCount = params.getContents().size();

        final int childIdsCount = contentToDuplicateWithChildrenSet.stream().map( parent -> ContentQueryWithChildren.create().
            contentService( this.contentService ).
            contentsIds( ContentIds.from( parent.getContentId() ) ).
            build().
            find().
            getTotalHits() ).mapToInt( Long::intValue ).sum();

        listener.setTotal( parentIdsCount + childIdsCount );

        final DuplicateRunnableTaskResult.Builder resultBuilder = this.getTaskResultBuilder();
        for ( DuplicateContentJson content : params.getContents() )
        {

            final ContentId contentId = ContentId.from( content.getContentId() );

            final DuplicateContentParams duplicateContentParams = DuplicateContentParams.create().
                contentId( contentId ).
                creator( authInfo.getUser().getKey() ).
                processor( getProcessor( content ) ).
                valueResolver( getValueResolver( content ) ).
                parent( getParent( content ) ).
                duplicateContentListener( listener ).
                includeChildren( content.getIncludeChildren() ).
                dependenciesToDuplicatePath( getDependenciesPath( contentId ) ).
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

    public static class Builder<T extends Builder<T>>
        extends AbstractRunnableTask.Builder<T>
    {
        private AuthenticationInfo authInfo;

        private DuplicateContentsJson params;

        public T authInfo( AuthenticationInfo authInfo )
        {
            this.authInfo = authInfo;
            return (T) this;
        }

        public T params( DuplicateContentsJson params )
        {
            this.params = params;
            return (T) this;
        }

        public DuplicateRunnableTask build()
        {
            return new DuplicateRunnableTask( this );
        }
    }
}
