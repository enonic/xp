package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentVersions;
import com.enonic.xp.content.FindContentVersionsResult;
import com.enonic.xp.node.FindNodeVersionsResult;
import com.enonic.xp.node.GetNodeVersionsParams;
import com.enonic.xp.node.NodeId;

public class FindContentVersionsCommand
    extends AbstractContentCommand
{
    private final ContentId contentId;

    private final int from;

    private final int size;

    private FindContentVersionsCommand( final Builder builder )
    {
        super( builder );
        contentId = builder.contentId;
        from = builder.from;
        size = builder.size;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public FindContentVersionsResult execute()
    {
        return doGetContentVersions();
    }

    private FindContentVersionsResult doGetContentVersions()
    {
        final NodeId nodeId = NodeId.from( this.contentId );

        final FindNodeVersionsResult findNodeVersionsResult = nodeService.findVersions( GetNodeVersionsParams.create().
            nodeId( nodeId ).
            from( this.from ).
            size( this.size ).
            build() );

        final FindContentVersionsResult.Builder findContentVersionsResultBuilder = FindContentVersionsResult.create();
        findContentVersionsResultBuilder.hits( findNodeVersionsResult.getHits() );
        findContentVersionsResultBuilder.totalHits( findNodeVersionsResult.getTotalHits() );
        findContentVersionsResultBuilder.from( findNodeVersionsResult.getFrom() );
        findContentVersionsResultBuilder.size( findNodeVersionsResult.getSize() );

        final ContentVersionFactory contentVersionFactory = new ContentVersionFactory( this.translator, this.nodeService );

        final ContentVersions contentVersions = contentVersionFactory.create( nodeId, findNodeVersionsResult.getNodeVersions() );

        findContentVersionsResultBuilder.contentVersions( contentVersions );

        return findContentVersionsResultBuilder.build();
    }


    public static final class Builder
        extends AbstractContentCommand.Builder<Builder>

    {
        private ContentId contentId;

        private int from;

        private int size;

        private Builder()
        {
        }

        public Builder contentId( ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder from( int from )
        {
            this.from = from;
            return this;
        }

        public Builder size( int size )
        {
            this.size = size;
            return this;
        }

        public FindContentVersionsCommand build()
        {
            return new FindContentVersionsCommand( this );
        }
    }
}
