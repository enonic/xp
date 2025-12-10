package com.enonic.xp.core.impl.content;

import java.util.Objects;

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
        final NodeId nodeId = NodeId.from( contentId );
        final NodeVersionId nodeVersionId = NodeVersionId.from( versionId );

        try
        {
            final Node node = nodeService.getByIdAndVersionId( nodeId, nodeVersionId );

            return ContentNodeTranslator.fromNodeWithAnyRootPath( node );
        }
        catch ( NodeNotFoundException e )
        {
            throw ContentNotFoundException.create()
                .contentId( contentId )
                .repositoryId( ContextAccessor.current().getRepositoryId() )
                .branch( ContextAccessor.current().getBranch() )
                .contentRoot( ContentNodeHelper.getContentRoot() )
                .cause( e )
                .build();
        }
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {

        private ContentId contentId;

        private ContentVersionId versionId;

        private Builder()
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

            Objects.requireNonNull( this.contentId, "contentId is required" );
            Objects.requireNonNull( this.versionId, "versionId is required" );
        }

        public GetContentByIdAndVersionIdCommand build()
        {
            validate();
            return new GetContentByIdAndVersionIdCommand( this );
        }
    }
}
