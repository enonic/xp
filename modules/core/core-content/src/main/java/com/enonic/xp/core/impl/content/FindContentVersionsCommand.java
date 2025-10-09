package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentVersions;
import com.enonic.xp.content.FindContentVersionsResult;
import com.enonic.xp.node.GetNodeVersionsParams;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.NodeVersionQueryResult;

public class FindContentVersionsCommand
    extends AbstractContentCommand
{
    private static final int DEFAULT_SIZE = 10;

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

        final NodeVersionQueryResult nodeVersionQueryResult = nodeService.findVersions( GetNodeVersionsParams.create().
            nodeId( nodeId ).
            from( this.from ).
            size( this.size ).
            build() );

        final FindContentVersionsResult.Builder findContentVersionsResultBuilder = FindContentVersionsResult.create().
            totalHits( nodeVersionQueryResult.getTotalHits() );

        final ContentVersionFactory contentVersionFactory = new ContentVersionFactory( this.nodeService );

        final ContentVersions.Builder contentVersionsBuilder = ContentVersions.create();

        for ( final NodeVersionMetadata nodeVersionMetadata : nodeVersionQueryResult.getNodeVersionMetadatas() )
        {
            contentVersionsBuilder.add( contentVersionFactory.create( nodeVersionMetadata ) );
        }

        return findContentVersionsResultBuilder.contentVersions( contentVersionsBuilder.build() ).build();
    }

    public static final class Builder
        extends AbstractContentCommand.Builder<Builder>

    {
        private ContentId contentId;

        private int from = 0;

        private int size = DEFAULT_SIZE;

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
