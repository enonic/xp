package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentVersionId;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodeVersionId;

public final class GetContentByIdAndVersionIdCommand
    extends AbstractContentCommand
{
    private final ContentId contentId;

    private final ContentVersionId versionId;

    private GetContentByIdAndVersionIdCommand( final Builder builder )
    {
        super( builder );
        this.contentId = builder.contentId;
        this.versionId = builder.versionId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Content execute()
    {
        final NodeId nodeId = NodeId.from( contentId.toString() );
        final NodeVersionId nodeVersionId = NodeVersionId.from( versionId.toString() );

        try
        {
            return getContentByIdAndVersionId( nodeId, nodeVersionId );
        }
        catch ( NodeNotFoundException e )
        {
            throw createContentNotFoundException();
        }
    }

    private Content getContentByIdAndVersionId( final NodeId nodeId, final NodeVersionId nodeVersionId )
    {
        final Node node = nodeService.getByIdAndVersionId( nodeId, nodeVersionId );

        final Content content = filter( translator.fromNode( node, true ) );

        if ( content != null )
        {
            return content;
        }

        throw createContentNotFoundException();
    }

    private ContentNotFoundException createContentNotFoundException()
    {
        return new ContentNotFoundException( contentId, versionId, ContextAccessor.current().getBranch() );
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {

        private ContentId contentId;

        private ContentVersionId versionId;

        public Builder()
        {
            super();
        }

        public Builder contentId( final ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder versionId( final ContentVersionId versionId )
        {
            this.versionId = versionId;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();

            Preconditions.checkNotNull( this.contentId );
            Preconditions.checkNotNull( this.versionId );
        }

        public GetContentByIdAndVersionIdCommand build()
        {
            validate();
            return new GetContentByIdAndVersionIdCommand( this );
        }
    }
}
