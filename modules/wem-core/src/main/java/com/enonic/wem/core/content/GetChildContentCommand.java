package com.enonic.wem.core.content;

import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Nodes;


final class GetChildContentCommand
    extends AbstractContentCommand
{
    private final ContentPath contentPath;

    private final boolean populateChildIds;

    private GetChildContentCommand( final Builder builder )
    {
        super( builder );

        this.contentPath = builder.contentPath;
        this.populateChildIds = builder.populateChildIds;
    }

    Contents execute()
    {
        final NodePath nodePath = ContentNodeHelper.translateContentPathToNodePath( this.contentPath );

        final Nodes nodes = nodeService.getByParent( nodePath, ContentConstants.DEFAULT_CONTEXT );

        final Contents contents = getTranslator().fromNodes( removeNonContentNodes( nodes ) );

        if ( populateChildIds && contents.isNotEmpty() )
        {
            return ChildContentIdsResolver.create().
                context( this.context ).
                nodeService( this.nodeService ).
                blobService( this.blobService ).
                contentTypeService( this.contentTypeService ).
                build().
                resolve( contents );
        }
        else
        {
            return contents;
        }
    }

    public static Builder create( final ContentPath contentPath )
    {
        return new Builder( contentPath );
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private ContentPath contentPath;

        private boolean populateChildIds = false;

        public Builder( final ContentPath contentPath )
        {
            this.contentPath = contentPath;
        }

        public Builder contentPath( final ContentPath contentPath )
        {
            this.contentPath = contentPath;
            return this;
        }

        public Builder populateChildIds( final boolean populateChildIds )
        {
            this.populateChildIds = populateChildIds;
            return this;
        }

        public GetChildContentCommand build()
        {
            return new GetChildContentCommand( this );
        }
    }

}
