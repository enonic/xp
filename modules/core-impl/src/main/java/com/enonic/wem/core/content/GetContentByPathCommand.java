package com.enonic.wem.core.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodePath;

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
        final NodePath nodePath = ContentNodeHelper.translateContentPathToNodePath( contentPath );

        final Node node = nodeService.getByPath( nodePath );

        if ( node == null )
        {
            throw new ContentNotFoundException( contentPath, ContextAccessor.current().getWorkspace() );
        }

        return translator.fromNode( node );
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
