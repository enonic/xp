package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.DuplicateContentException;
import com.enonic.xp.content.DuplicateContentListener;
import com.enonic.xp.content.DuplicateContentParams;
import com.enonic.xp.content.DuplicateContentsResult;
import com.enonic.xp.node.DuplicateNodeListener;
import com.enonic.xp.node.DuplicateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;

final class DuplicateContentCommand
    extends AbstractContentCommand
    implements DuplicateNodeListener
{
    private final DuplicateContentParams params;

    private final DuplicateContentListener duplicateContentListener;

    private DuplicateContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.duplicateContentListener = builder.duplicateContentListener;
    }

    public static Builder create( final DuplicateContentParams params )
    {
        return new Builder( params );
    }

    DuplicateContentsResult execute()
    {
        params.validate();

        try
        {
            final DuplicateContentsResult duplicatedContents = doExecute();
            return duplicatedContents;
        }
        catch ( Exception e )
        {
            throw new DuplicateContentException( e.getMessage() );
        }
    }

    private DuplicateContentsResult doExecute()
    {
        final NodeId sourceNodeId = NodeId.from( params.getContentId() );
        final Node sourceNode = nodeService.getById( sourceNodeId );
        if ( sourceNode == null )
        {
            throw new IllegalArgumentException( String.format( "Content with id [%s] not found", params.getContentId() ) );
        }

        final DuplicateNodeParams duplicateNodeParams =
            DuplicateNodeParams.create().duplicateListener( this ).nodeId( sourceNodeId ).processor(
                new DuplicateContentProcessor() ).includeChildren( params.getIncludeChildren() ).build();

        final Node duplicatedNode = nodeService.duplicate( duplicateNodeParams );

        final Content duplicatedContent = translator.fromNode( duplicatedNode, true );

        String contentName = duplicatedContent.getDisplayName();
        ContentId contentId = duplicatedContent.getId();

        final DuplicateContentsResult result = DuplicateContentsResult.create().
            setContentName( contentName ).
            addDuplicated( contentId ).
            build();

        return result;
    }

    @Override
    public void nodesDuplicated( final int count )
    {
        if ( duplicateContentListener != null )
        {
            duplicateContentListener.contentDuplicated( count );
        }
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private final DuplicateContentParams params;

        private DuplicateContentListener duplicateContentListener;

        public Builder( final DuplicateContentParams params )
        {
            this.params = params;
        }

        public Builder duplicateListener( final DuplicateContentListener duplicateListener )
        {
            this.duplicateContentListener = duplicateListener;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( params );
        }

        public DuplicateContentCommand build()
        {
            validate();
            return new DuplicateContentCommand( this );
        }
    }

}
