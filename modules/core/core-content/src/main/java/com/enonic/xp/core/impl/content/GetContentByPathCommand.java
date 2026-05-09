package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeSearchPreference;

import static java.util.Objects.requireNonNull;

final class GetContentByPathCommand
    extends AbstractContentCommand
{
    private final ContentPath contentPath;

    private final NodeSearchPreference searchPreference;

    private GetContentByPathCommand( final Builder builder )
    {
        super( builder );
        requireNonNull( builder.contentPath, "contentPath is required" );
        this.contentPath = builder.contentPath;
        this.searchPreference = builder.searchPreference;
    }

    Content execute()
    {
        final NodePath nodePath = ContentNodeHelper.translateContentPathToNodePath( contentPath );

        final Node node = searchPreference != null ? nodeService.getByPath( nodePath, searchPreference ) : nodeService.getByPath( nodePath );

        if ( node == null )
        {
            return null;
        }

        final Content content = ContentNodeTranslator.fromNode( node );
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

        private NodeSearchPreference searchPreference;

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
            requireNonNull( contentPath, "contentPath is required" );
        }

        Builder searchPreference( final NodeSearchPreference searchPreference )
        {
            this.searchPreference = searchPreference;
            return this;
        }

        public GetContentByPathCommand build()
        {
            validate();
            return new GetContentByPathCommand( this );
        }

    }

}
