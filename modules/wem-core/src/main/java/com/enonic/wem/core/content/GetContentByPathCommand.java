package com.enonic.wem.core.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.context.Context2;
import com.enonic.wem.core.entity.NoNodeAtPathFoundException;
import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.entity.NodePath;

final class GetContentByPathCommand
    extends AbstractContentCommand
{
    private final ContentPath contentPath;

    private GetContentByPathCommand( final Builder builder )
    {
        super( builder );
        this.contentPath = builder.contentPath;
    }

    Content execute()
    {
        final NodePath nodePath = ContentNodeHelper.translateContentPathToNodePath( contentPath );

        try
        {
            final Node node = nodeService.getByPath( nodePath );
            return translator.fromNode( node );
        }
        catch ( NoNodeAtPathFoundException e )
        {
            throw new ContentNotFoundException( contentPath, Context2.current().getWorkspace() );
        }
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
