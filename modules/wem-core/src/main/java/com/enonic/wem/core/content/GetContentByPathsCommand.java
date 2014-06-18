package com.enonic.wem.core.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.entity.NoNodeAtPathFoundException;
import com.enonic.wem.api.entity.NodePaths;
import com.enonic.wem.api.entity.Nodes;


final class GetContentByPathsCommand
    extends AbstractContentCommand
{
    private final ContentPaths contentPaths;

    private GetContentByPathsCommand( final Builder builder )
    {
        super( builder );
        this.contentPaths = builder.contentPaths;
    }

    Contents execute()
    {
        final Contents contents;

        try
        {
            contents = doExecute();
        }
        catch ( NoNodeAtPathFoundException ex )
        {
            throw new ContentNotFoundException( ContentPath.from( ex.getPath().toString() ), this.context.getWorkspace() );
        }

        return contents;
    }

    private Contents doExecute()
    {
        final NodePaths paths = ContentNodeHelper.translateContentPathsToNodePaths( contentPaths );
        final Nodes nodes = nodeService.getByPaths( paths, this.context );

        return translator.fromNodes( nodes );
    }

    public static Builder create( final ContentPaths contentPaths )
    {
        return new Builder( contentPaths );
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private final ContentPaths contentPaths;

        public Builder( final ContentPaths contentPaths )
        {
            this.contentPaths = contentPaths;
        }

        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( contentPaths );
        }

        public GetContentByPathsCommand build()
        {
            validate();
            return new GetContentByPathsCommand( this );
        }
    }
}
