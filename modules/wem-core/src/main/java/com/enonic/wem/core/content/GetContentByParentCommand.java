package com.enonic.wem.core.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Nodes;


final class GetContentByParentCommand
    extends AbstractContentCommand
{
    private final ContentPath parentPath;

    private final boolean populateChildIds;

    private GetContentByParentCommand( final Builder builder )
    {
        super( builder );

        this.parentPath = builder.parentPath;
        this.populateChildIds = builder.populateChildIds;
    }

    public static Builder create( final ContentPath contentPath )
    {
        return new Builder( contentPath );
    }

    Contents execute()
    {
        final NodePath nodePath = ContentNodeHelper.translateContentPathToNodePath( this.parentPath );

        final Nodes nodes = nodeService.getByParent( nodePath, this.context );

        final Contents contents = translator.fromNodes( removeNonContentNodes( nodes ) );

        if ( populateChildIds && contents.isNotEmpty() )
        {
            return ChildContentIdsResolver.create().
                context( this.context ).
                nodeService( this.nodeService ).
                blobService( this.blobService ).
                contentTypeService( this.contentTypeService ).
                translator( this.translator ).
                build().
                resolve( contents );
        }
        else
        {
            return contents;
        }
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private ContentPath parentPath;

        private boolean populateChildIds = true;

        public Builder( final ContentPath parentPath )
        {
            this.parentPath = parentPath;
        }

        public Builder contentPath( final ContentPath parentPath )
        {
            this.parentPath = parentPath;
            return this;
        }

        public Builder populateChildIds( final boolean populateChildIds )
        {
            this.populateChildIds = populateChildIds;
            return this;
        }

        void validate()
        {
            Preconditions.checkNotNull( parentPath );
        }

        public GetContentByParentCommand build()
        {
            validate();
            return new GetContentByParentCommand( this );
        }
    }

}
