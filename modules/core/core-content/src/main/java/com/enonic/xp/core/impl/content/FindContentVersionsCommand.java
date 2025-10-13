package com.enonic.xp.core.impl.content;

import java.util.List;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.content.ContentVersionId;
import com.enonic.xp.content.ContentVersions;
import com.enonic.xp.content.FindContentVersionsResult;
import com.enonic.xp.node.Attributes;
import com.enonic.xp.node.GetNodeVersionsParams;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.util.GenericValue;

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
        final NodeVersionQueryResult nodeVersionQueryResult = nodeService.findVersions(
            GetNodeVersionsParams.create().nodeId( NodeId.from( this.contentId ) ).from( this.from ).size( this.size ).build() );

        final FindContentVersionsResult.Builder findContentVersionsResultBuilder =
            FindContentVersionsResult.create().totalHits( nodeVersionQueryResult.getTotalHits() );

        return findContentVersionsResultBuilder.contentVersions(
                nodeVersionQueryResult.getNodeVersionMetadatas().stream().map( this::createVersion ).collect( ContentVersions.collector() ) )
            .build();
    }

    public ContentVersion createVersion( final NodeVersionMetadata nodeVersionMetadata )
    {
        final Attributes attributes = nodeVersionMetadata.getAttributes();

        final ContentVersion.Builder builder = ContentVersion.create()
            .contentId( ContentId.from( nodeVersionMetadata.getNodeId() ) )
            .versionId( ContentVersionId.from( nodeVersionMetadata.getNodeVersionId().toString() ) )
            .path( ContentNodeHelper.translateNodePathToContentPath( nodeVersionMetadata.getNodePath() ) )
            .timestamp( nodeVersionMetadata.getTimestamp() );

        if ( attributes != null )
        {
            attributes.list()
                .stream()
                .filter( v -> v.getKey().startsWith( "content." ) )
                .map( v -> new ContentVersion.Action( v.getKey(),
                                                      v.getValue().optional( "fields" ).map( GenericValue::asStringList ).orElse( List.of() ),
                                                      ContentAttributesHelper.getUser( v.getValue() ), ContentAttributesHelper.getOpTime( v.getValue() ) ) )
                .forEach( builder::addAction );
        }

        if ( nodeVersionMetadata.getNodeCommitId() != null )
        {
            final NodeCommitEntry nodeCommitEntry = nodeService.getCommit( nodeVersionMetadata.getNodeCommitId() );
            if ( nodeCommitEntry != null )
            {
                final String commitMessage = nodeCommitEntry.getMessage();
                builder.comment( getCommentPart( commitMessage ) );
            }
        }

        return builder.build();
    }

    private static String getCommentPart( final String message )
    {
        if ( message.startsWith( ContentConstants.PUBLISH_COMMIT_PREFIX + ContentConstants.PUBLISH_COMMIT_PREFIX_DELIMITER ) )
        {
            return message.substring(
                ContentConstants.PUBLISH_COMMIT_PREFIX.length() + ContentConstants.PUBLISH_COMMIT_PREFIX_DELIMITER.length() );
        }
        else if ( message.startsWith( ContentConstants.ARCHIVE_COMMIT_PREFIX + ContentConstants.PUBLISH_COMMIT_PREFIX_DELIMITER ) )
        {
            return message.substring(
                ContentConstants.ARCHIVE_COMMIT_PREFIX.length() + ContentConstants.PUBLISH_COMMIT_PREFIX_DELIMITER.length() );
        }
        else
        {
            return null;
        }
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
