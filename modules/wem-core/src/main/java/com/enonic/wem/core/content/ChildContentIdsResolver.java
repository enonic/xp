package com.enonic.wem.core.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.GetContentByParentParams;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.core.index.query.QueryService;

final class ChildContentIdsResolver
{
    private final NodeService nodeService;

    private final ContentTypeService contentTypeService;

    private final BlobService blobService;

    private final Context context;

    private final ContentNodeTranslator translator;

    private final QueryService queryService;

    private ChildContentIdsResolver( final Builder builder )
    {
        this.nodeService = builder.nodeService;
        this.contentTypeService = builder.contentTypeService;
        this.blobService = builder.blobService;
        this.context = builder.context;
        this.translator = builder.translator;
        this.queryService = builder.queryService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    Content resolve( final Content content )
    {

        final GetContentByParentParams getContentByParentParams = GetContentByParentParams.create().
            parentPath( content.getPath() ).
            from( 0 ).
            size( 1 ).
            build();

        final Contents children = GetContentByParentCommand.create( getContentByParentParams ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            context( this.context ).
            blobService( this.blobService ).
            translator( this.translator ).
            queryService( this.queryService ).
            populateChildIds( false ).
            build().
            execute();

        if ( children.isNotEmpty() )
        {
            return populateWithChildrenIds( content, children );
        }
        else
        {
            return content;
        }
    }

    private Content populateWithChildrenIds( final Content content, final Contents children )
    {
        final Content.Builder builder = Content.newContent( content );
        for ( Content child : children )
        {
            builder.addChildId( child.getId() );
        }

        return builder.build();
    }

    Contents resolve( final Contents contents )
    {
        final Contents.Builder resolvedContent = new Contents.Builder();

        for ( final Content content : contents )
        {
            resolvedContent.add( resolve( content ) );
        }

        return resolvedContent.build();
    }

    public static class Builder
    {
        private NodeService nodeService;

        private ContentTypeService contentTypeService;

        private BlobService blobService;

        private Context context;

        private ContentNodeTranslator translator;

        private QueryService queryService;

        public Builder nodeService( final NodeService nodeService )
        {
            this.nodeService = nodeService;
            return this;
        }

        public Builder queryService( final QueryService queryService )
        {
            this.queryService = queryService;
            return this;
        }

        public Builder contentTypeService( final ContentTypeService contentTypeService )
        {
            this.contentTypeService = contentTypeService;
            return this;
        }

        public Builder blobService( final BlobService blobService )
        {
            this.blobService = blobService;
            return this;
        }

        public Builder translator( final ContentNodeTranslator translator )
        {
            this.translator = translator;
            return this;
        }

        public Builder context( final Context context )
        {
            this.context = context;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( translator );
            Preconditions.checkNotNull( blobService );
            Preconditions.checkNotNull( contentTypeService );
            Preconditions.checkNotNull( nodeService );
            Preconditions.checkNotNull( queryService );
        }

        public ChildContentIdsResolver build()
        {
            validate();
            return new ChildContentIdsResolver( this );
        }
    }


}
