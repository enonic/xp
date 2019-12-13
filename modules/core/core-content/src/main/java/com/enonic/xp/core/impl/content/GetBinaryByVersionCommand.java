package com.enonic.xp.core.impl.content;

import com.google.common.io.ByteSource;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentVersionId;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.util.BinaryReference;

public class GetBinaryByVersionCommand
    extends AbstractContentCommand
{
    private final ContentId contentId;

    private final ContentVersionId contentVersionId;

    private final BinaryReference binaryReference;

    private GetBinaryByVersionCommand( final Builder builder )
    {
        super( builder );
        this.contentId = builder.contentId;
        this.contentVersionId = builder.contentVersionId;
        this.binaryReference = builder.binaryReference;
    }

    public ByteSource execute()
    {
        return nodeService.getBinary( NodeId.from( contentId.toString() ), NodeVersionId.from( contentVersionId.toString() ),
                                      binaryReference );
    }

    public static Builder create( final ContentId contentId, final ContentVersionId contentVersionId,
                                  final BinaryReference binaryReference )
    {
        return new Builder( contentId, contentVersionId, binaryReference );
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {

        private ContentId contentId;

        private ContentVersionId contentVersionId;

        private BinaryReference binaryReference;

        public Builder( final ContentId contentId, final ContentVersionId contentVersionId, final BinaryReference binaryReference )
        {
            super();
            this.contentId = contentId;
            this.contentVersionId = contentVersionId;
            this.binaryReference = binaryReference;
        }

        public Builder contentId( final ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder contentVersionId( final ContentVersionId contentVersionId )
        {
            this.contentVersionId = contentVersionId;
            return this;
        }

        public Builder binaryReference( final BinaryReference binaryReference )
        {
            this.binaryReference = binaryReference;
            return this;
        }

        public GetBinaryByVersionCommand build()
        {
            return new GetBinaryByVersionCommand( this );
        }

    }

}
