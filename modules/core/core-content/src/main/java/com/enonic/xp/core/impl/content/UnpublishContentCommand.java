package com.enonic.xp.core.impl.content;

import java.time.Instant;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.UnpublishContentParams;
import com.enonic.xp.content.UnpublishContentsResult;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.UpdateNodeParams;

public class UnpublishContentCommand
    extends AbstractContentCommand
{
    private final UnpublishContentParams params;

    private UnpublishContentCommand( final Builder builder )
    {
        super( builder );

        this.params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public UnpublishContentsResult execute()
    {
        final Context context = ContextAccessor.current();

        final Context unpublishContext = ContextBuilder.from( context ).branch( ContentConstants.BRANCH_MASTER ).build();

        return unpublishContext.callWith( this::unpublish );
    }

    private UnpublishContentsResult unpublish()
    {
        final ContentIds.Builder contentBuilder = ContentIds.create();

        for ( final ContentId contentId : this.params.getContentIds() )
        {
            recursiveUnpublish( NodeId.from( contentId ), contentBuilder );
        }

        final ContentIds contentIds = contentBuilder.build();
        final Context draftContext = ContextBuilder.from( ContextAccessor.current() ).
            branch( ContentConstants.BRANCH_DRAFT ).
            build();

        draftContext.callWith( () -> removePublishInfo( contentIds ) );

        final UnpublishContentsResult.Builder resultBuilder = UnpublishContentsResult.create().
            addUnpublished( contentIds );
        if ( contentIds.getSize() == 1 )
        {
            draftContext.callWith( () -> resultBuilder.setContentPath( this.getContent( contentIds.first() ).getPath() ) );
        }

        return resultBuilder.build();
    }

    private void recursiveUnpublish( final NodeId nodeId, final ContentIds.Builder contentsBuilder )
    {
        final FindNodesByParentResult result = this.nodeService.findByParent( FindNodesByParentParams.create().parentId( nodeId ).build() );

        result.getNodeIds().forEach( id -> recursiveUnpublish( id, contentsBuilder ) );
        final NodeIds nodes = this.nodeService.deleteById( nodeId );
        if ( nodes.isNotEmpty() )
        {
            if ( params.getPublishContentListener() != null )
            {
                params.getPublishContentListener().contentPushed( 1 );
            }
            contentsBuilder.add( ContentId.from( nodes.first() ) );
        }
    }

    private Void removePublishInfo( final ContentIds contentIds )
    {
        final Instant now = Instant.now();
        for ( final ContentId contentId : contentIds )
        {
            this.nodeService.update( UpdateNodeParams.create().
                editor( toBeEdited -> {

                    if ( toBeEdited.data.getInstant( ContentPropertyNames.PUBLISH_INFO + PropertyPath.ELEMENT_DIVIDER + ContentPropertyNames.PUBLISH_FROM ) != null )
                    {
                        PropertySet publishInfo = toBeEdited.data.getSet( ContentPropertyNames.PUBLISH_INFO );

                        publishInfo.removeProperties( ContentPropertyNames.PUBLISH_FROM );

                        publishInfo.removeProperties( ContentPropertyNames.PUBLISH_TO );

                        if ( publishInfo.getInstant( ContentPropertyNames.PUBLISH_FIRST ).isAfter( now ) )
                        {
                            publishInfo.removeProperty( ContentPropertyNames.PUBLISH_FIRST );
                        }
                    }
                } ).
                id( NodeId.from( contentId ) ).
                build() );

            commitUnpublishedNode( contentId );
        }
        return null;
    }

    private void commitUnpublishedNode( final ContentId contentId )
    {
        final NodeCommitEntry commitEntry = NodeCommitEntry.create().
            message( ContentConstants.UNPUBLISH_COMMIT_PREFIX ).
            build();

        nodeService.refresh( RefreshMode.ALL );
        nodeService.commit( commitEntry, NodeIds.from( contentId.toString() ) );
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private UnpublishContentParams params;

        public Builder params( final UnpublishContentParams params )
        {
            this.params = params;
            return this;
        }

        @Override
        void validate()
        {
            Preconditions.checkNotNull( params );
        }

        public UnpublishContentCommand build()
        {
            validate();
            return new UnpublishContentCommand( this );
        }
    }

}

