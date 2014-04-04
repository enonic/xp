package com.enonic.wem.core.content;

import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.command.content.GetChildContent;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.core.command.CommandContext;

class ChildContentIdsResolver
{
    private final CommandContext context;

    private final NodeService nodeService;

    private final ContentTypeService contentTypeService;

    private final BlobService blobService;

    ChildContentIdsResolver( final CommandContext context,
                             final NodeService nodeService,
                             final ContentTypeService contentTypeService,
                             final BlobService blobService )
    {
        this.context = context;
        this.nodeService = nodeService;
        this.contentTypeService = contentTypeService;
        this.blobService = blobService;
    }

    Content resolve( final Content content )
        throws Exception
    {
        final Contents children = new GetChildContentService(
            context,
            new GetChildContent().parentPath( content.getPath() ),
            nodeService,
            contentTypeService,
            blobService ).execute();

        if ( children.isNotEmpty() )
        {
            final Content.Builder builder = Content.newContent( content );
            for ( Content child : children )
            {
                builder.addChildId( child.getId() );
            }
            return builder.build();
        }
        else
        {
            return content;
        }
    }

    Contents resolve( final Contents contents )
        throws Exception
    {
        final Contents.Builder builder = new Contents.Builder();

        for ( final Content content : contents )
        {
            builder.add( resolve( content ) );
        }

        return builder.build();
    }

}
