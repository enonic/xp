package com.enonic.xp.core.impl.content;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareContentResult;
import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.content.PushContentsResult;
import com.enonic.xp.content.ResolvePublishDependenciesResult;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.node.RefreshMode;

public class PushContentCommand
    extends AbstractContentCommand
{
    private final ContentIds contentIds;

    private final ContentIds excludedContentIds;

    private final Branch target;

    private final boolean includeDependencies;

    private final PushContentsResult.Builder resultBuilder;

    private final boolean includeChildren;

    private final static Logger LOG = LoggerFactory.getLogger( PushContentCommand.class );

    private PushContentCommand( final Builder builder )
    {
        super( builder );
        this.contentIds = builder.contentIds;
        this.excludedContentIds = builder.excludedContentIds;
        this.target = builder.target;
        this.includeDependencies = builder.includeDependencies;
        this.includeChildren = builder.includeChildren;
        this.resultBuilder = PushContentsResult.create();
    }

    PushContentsResult execute()
    {
        this.nodeService.refresh( RefreshMode.ALL );

        if ( includeDependencies )
        {
            pushAndDelete( getWithDependents() );
        }
        else
        {
            pushAndDelete( this.contentIds );
        }

        this.nodeService.refresh( RefreshMode.ALL );

        return resultBuilder.build();
    }

    private void pushAndDelete( final ContentIds contentIds )
    {
        NodeIds.Builder pushNodesIds = NodeIds.create();
        NodeIds.Builder deletedNodesIds = NodeIds.create();

        LOG.info( "Ok, fetched " + contentIds.getSize() + " content ids that should be published, now get contentComparisons" );

        final Stopwatch compareContentTimer = Stopwatch.createStarted();
        final CompareContentResults contentsComparisons = CompareContentsCommand.create().
            nodeService( this.nodeService ).
            contentIds( contentIds ).
            target( this.target ).
            build().
            execute();
        LOG.info( "Compare content done in " + compareContentTimer.stop().toString() );

        for ( CompareContentResult compareResult : contentsComparisons )
        {
            if ( compareResult.getCompareStatus() == CompareStatus.PENDING_DELETE )
            {
                deletedNodesIds.add( NodeId.from( compareResult.getContentId() ) );
            }
            else
            {
                pushNodesIds.add( NodeId.from( compareResult.getContentId() ) );
            }
        }

        LOG.info( "Done sorting, now do other stuff, like validate" );
        final Stopwatch timer = Stopwatch.createStarted();

        final Stopwatch transformTimer = Stopwatch.createStarted();
        final ContentIds pushContentsIds = ContentIds.from( pushNodesIds.build().stream().
            map( ( n ) -> ContentId.from( n.toString() ) ).
            toArray( ContentId[]::new ) );
        LOG.info( "Transform to contentIds from pushNodesIds [" + transformTimer.stop().toString() );

        final boolean validContents = checkIfAllContentsValid( pushContentsIds );

        if ( !validContents )
        {
            return;
        }

        LOG.info( "Validation is done in " + timer.stop().toString() );

        doPushNodes( pushNodesIds.build() );
        doDeleteNodes( deletedNodesIds.build() );
    }

    private boolean checkIfAllContentsValid( final ContentIds pushContentsIds )
    {
        return CheckContentsValidCommand.create().
            translator( this.translator ).
            nodeService( this.nodeService ).
            eventPublisher( this.eventPublisher ).
            contentTypeService( this.contentTypeService ).
            contentIds( pushContentsIds ).
            build().
            execute();
/*
        final Stopwatch contentsToPushTimer = Stopwatch.createStarted();
        final Contents contentsToPush = getContentByIds( new GetContentByIdsParams( pushContentsIds ).setGetChildrenIds( false ) );
        LOG.info( "contentsToPushTimer [" + contentsToPushTimer.stop().toString() );

        final Stopwatch validCheck = Stopwatch.createStarted();
        final boolean validContents = ensureValidContents( contentsToPush );
        LOG.info( "validCheck [" + validCheck.stop().toString() );
        return validContents;
*/
    }

    private ContentIds getWithDependents()
    {
        final ResolvePublishDependenciesResult resolvedResult = ResolvePublishDependenciesCommand.create().
            contentIds( this.contentIds ).
            excludedContentIds( this.excludedContentIds ).
            includeChildren( this.includeChildren ).
            target( this.target ).
            contentTypeService( this.contentTypeService ).
            eventPublisher( this.eventPublisher ).
            translator( this.translator ).
            nodeService( this.nodeService ).
            build().
            execute();

        return resolvedResult.contentIds();
    }


    private void doPushNodes( final NodeIds nodesToPush )
    {
        if ( nodesToPush.isEmpty() )
        {
            return;
        }

        final Stopwatch nodeServicePushTimer = Stopwatch.createStarted();
        final PushNodesResult pushNodesResult = nodeService.push( nodesToPush, this.target );
        System.out.println( "nodeService.push: " + nodeServicePushTimer.stop().toString() );

        this.resultBuilder.setPushed( ContentNodeHelper.toContentIds( NodeIds.from( pushNodesResult.getSuccessful().getKeys() ) ) );
    }

    private void doDeleteNodes( final NodeIds nodeIdsToDelete )
    {
        this.resultBuilder.setDeleted( ContentNodeHelper.toContentIds( NodeIds.from( nodeIdsToDelete ) ) );

        final Context currentContext = ContextAccessor.current();
        deleteNodesInContext( nodeIdsToDelete, currentContext );
        deleteNodesInContext( nodeIdsToDelete, ContextBuilder.from( currentContext ).
            branch( target ).
            build() );
    }

    private void deleteNodesInContext( final NodeIds nodeIds, final Context context )
    {
        context.callWith( () -> {
            nodeIds.forEach( nodeService::deleteById );
            return null;
        } );
    }


    private boolean ensureValidContents( final Contents contents )
    {
        return contents.stream().allMatch( Content::isValid );
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

        private ContentIds excludedContentIds;

        private Branch target;

        private boolean includeDependencies = true;

        private boolean includeChildren = true;

        public Builder contentIds( final ContentIds contentIds )
        {
            this.contentIds = contentIds;
            return this;
        }

        public Builder excludedContentIds( final ContentIds excludedContentIds )
        {
            this.excludedContentIds = excludedContentIds;
            return this;
        }

        public Builder target( final Branch target )
        {
            this.target = target;
            return this;
        }

        public Builder includeDependencies( final boolean includeDependencies )
        {
            this.includeDependencies = includeDependencies;
            return this;
        }

        public Builder includeChildren( final boolean includeChildren )
        {
            this.includeChildren = includeChildren;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( target );
            Preconditions.checkNotNull( contentIds );
        }

        public PushContentCommand build()
        {
            validate();
            return new PushContentCommand( this );
        }

    }
}