package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentVersionId;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;

@Deprecated
public class GetContentByPathAndVersionIdCommand
    extends AbstractContentCommand
{

    private final ContentPath contentPath;

    private final ContentVersionId contentVersionId;


    private GetContentByPathAndVersionIdCommand( final Builder builder )
    {
        super( builder );

        this.contentPath = builder.contentPath;
        this.contentVersionId = builder.contentVersionId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Content execute()
    {
        final NodePath nodePath = ContentNodeHelper.translateContentPathToNodePath( contentPath );
        final NodeVersionId nodeVersionId = NodeVersionId.from( contentVersionId );

        try
        {
            final Node node = nodeService.getByPathAndVersionId( nodePath, nodeVersionId );

            final Content content = filter( translator.fromNode( node, true ) );

            if ( content != null )
            {
                return content;
            }

            throw createContentNotFoundException();
        }
        catch ( NodeNotFoundException e )
        {
            throw createContentNotFoundException();
        }
    }

    private ContentNotFoundException createContentNotFoundException()
    {
        return new ContentNotFoundException( contentPath, contentVersionId, ContextAccessor.current().getBranch() );
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {

        private ContentPath contentPath;

        private ContentVersionId contentVersionId;

        public Builder()
        {
            super();
        }

        public Builder contentPath( final ContentPath contentPath )
        {
            this.contentPath = contentPath;
            return this;
        }

        public Builder versionId( final ContentVersionId contentVersionId )
        {
            this.contentVersionId = contentVersionId;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();

            Preconditions.checkNotNull( this.contentPath );
            Preconditions.checkNotNull( this.contentVersionId );
        }

        public GetContentByPathAndVersionIdCommand build()
        {
            validate();
            return new GetContentByPathAndVersionIdCommand( this );
        }

    }

}
