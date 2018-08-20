package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;

final class GetContentByPathCommand
    extends AbstractContentCommand
{
    private final ContentPath contentPath;

    private GetContentByPathCommand( final Builder builder )
    {
        super( builder );
        Preconditions.checkArgument( builder.contentPath.isAbsolute(), "contentPath must be absolute: " + builder.contentPath );
        this.contentPath = builder.contentPath;
    }

    Content execute()
    {
        final Node node = getNode();
        if ( node == null )
        {
            throw new ContentNotFoundException( contentPath, ContextAccessor.current().getBranch() );
        }

        final Content content = translator.fromNode( node, true );
        final Content filteredContent = filter( content );
        if ( filteredContent == null )
        {
            throw new ContentNotFoundException( contentPath, ContextAccessor.current().getBranch() );
        }
        return filteredContent;
    }

    private Node getNode()
    {
        if ( ContentPath.ROOT.equals( contentPath ) )
        {
            return null;
        }
        final NodePath nodePath = ContentNodeHelper.translateContentPathToNodePath( contentPath );
        return nodeService.getByPath( nodePath );
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

        public Builder( final ContentPath contentPath )
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
