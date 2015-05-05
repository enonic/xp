package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.content.ResolvePublishDependenciesResult;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.node.ResolveSyncWorkResults;
import com.enonic.xp.node.SyncWorkResolverParams;

public class ResolvePublishDependenciesCommand
    extends AbstractContentCommand
{
    private final ContentIds contentIds;

    private final Branch target;

    private final ResolvePublishDependenciesResult.Builder resultBuilder;

    private ResolvePublishDependenciesCommand( final Builder builder )
    {
        super( builder );
        this.contentIds = builder.contentIds;
        this.target = builder.target;
        this.resultBuilder = ResolvePublishDependenciesResult.create();
    }

    ResolvePublishDependenciesResult execute()
    {
        resolveDependencies();
        return resultBuilder.build();
    }

    private void resolveDependencies()
    {
        final ResolveSyncWorkResults syncWorkResultsWithChildren = resolveSyncWorkResults( true );
        final ResolveSyncWorkResults syncWorkResultsWithoutChildren = resolveSyncWorkResults( false );

        appendSyncWorkResultsToResult( syncWorkResultsWithChildren, syncWorkResultsWithoutChildren );
    }

    private ResolveSyncWorkResults resolveSyncWorkResults( boolean includeChildren )
    {
        final ResolveSyncWorkResults.Builder resultsBuilder = ResolveSyncWorkResults.create();

        for ( final ContentId contentId : this.contentIds )
        {
            final ResolveSyncWorkResult syncWorkResult = getWorkResult( contentId, includeChildren );

            resultsBuilder.add( syncWorkResult );
        }
        return resultsBuilder.build();
    }

    private ResolveSyncWorkResult getWorkResult( final ContentId contentId, boolean includeChildren )
    {
        return nodeService.resolveSyncWork( SyncWorkResolverParams.create().
            includeChildren( includeChildren ).
            nodeId( NodeId.from( contentId.toString() ) ).
            branch( this.target ).
            build() );
    }

    private void appendSyncWorkResultsToResult( final ResolveSyncWorkResults syncWorkResultsWithChildren,
                                                final ResolveSyncWorkResults syncWorkResultsWithoutChildren )
    {
        this.resultBuilder.pushContentRequestsWithChildren( PushContentRequestsFactory.create( syncWorkResultsWithChildren ) );
        this.resultBuilder.pushContentRequestsWithoutChildren( PushContentRequestsFactory.create( syncWorkResultsWithoutChildren ) );
    }

    private Contents getContentByIds( final GetContentByIdsParams getContentParams )
    {
        return GetContentByIdsCommand.create( getContentParams ).
            nodeService( this.nodeService ).
            translator( this.translator ).
            contentTypeService( this.contentTypeService ).
            eventPublisher( this.eventPublisher ).
            build().
            execute();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private ContentIds contentIds;

        private Branch target;

        public Builder contentIds( final ContentIds contentIds )
        {
            this.contentIds = contentIds;
            return this;
        }

        public Builder target( final Branch target )
        {
            this.target = target;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( target );
            Preconditions.checkNotNull( contentIds );
        }

        public ResolvePublishDependenciesCommand build()
        {
            validate();
            return new ResolvePublishDependenciesCommand( this );
        }

    }
}
