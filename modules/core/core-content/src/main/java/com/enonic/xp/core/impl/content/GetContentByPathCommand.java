package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;

final class GetContentByPathCommand
    extends AbstractContentCommand
{
    private final ContentPath contentPath;

    private GetContentByPathCommand( final Builder builder )
    {
        super( builder );
        Preconditions.checkNotNull( builder.contentPath, "contentPath cannot be null" );
        this.contentPath = builder.contentPath;
    }

    Content execute()
    {
        final NodePath nodePath = ContentNodeHelper.translateContentPathToNodePath( contentPath );

        final Node node = nodeService.getByPath( nodePath );

        if ( node == null )
        {
            return null;
        }

        final Content content = translator.fromNode( node, true );
        return filter( content );
    }

    static Builder create( final ContentPath contentPath, final AbstractContentCommand source )
    {
        return new Builder( contentPath, source );
    }

    public static Builder create( final ContentPath contentPath )
    {
        return new Builder( contentPath );
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private final ContentPath contentPath;

        Builder( final ContentPath contentPath )
        {
            this.contentPath = contentPath;
        }

        Builder( final ContentPath contentPath, AbstractContentCommand source )
        {
            super( source );
            this.contentPath = contentPath;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( contentPath );
        }

        public GetContentByPathCommand build()
        {
            validate();
            return new GetContentByPathCommand( this );
        }

    }

}
