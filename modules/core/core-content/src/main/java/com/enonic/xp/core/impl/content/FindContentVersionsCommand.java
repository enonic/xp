package com.enonic.xp.core.impl.content;

import java.util.List;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.content.ContentVersionId;
import com.enonic.xp.content.ContentVersions;
import com.enonic.xp.content.FindContentVersionsResult;
import com.enonic.xp.core.impl.content.serializer.PublishInfoSerializer;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.Attributes;
import com.enonic.xp.node.GetNodeVersionsParams;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersion;
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

        final ContentVersions.Builder contentVersionsBuilder = ContentVersions.create();

        for ( final NodeVersionMetadata nodeVersionMetadata : nodeVersionQueryResult.getNodeVersionMetadatas() )
        {
            contentVersionsBuilder.add( createVersion( nodeVersionMetadata ) );
        }

        return findContentVersionsResultBuilder.contentVersions( contentVersionsBuilder.build() ).build();
    }

    public ContentVersion createVersion( final NodeVersionMetadata nodeVersionMetadata )
    {
        final Attributes attributes = nodeVersionMetadata.getAttributes();

        final ContentVersion.Builder builder = ContentVersion.create()
            .id( ContentVersionId.from( nodeVersionMetadata.getNodeVersionId().toString() ) )
            .path( ContentNodeHelper.translateNodePathToContentPath( nodeVersionMetadata.getNodePath() ) )
            .timestamp( nodeVersionMetadata.getTimestamp() )
            .attributes( attributes );

        if ( attributes != null )
        {
            final GenericValue publishAttr = attributes.get( ContentAttributesHelper.PUBLISH_KEY );
            final GenericValue unpublishAttr = attributes.get( ContentAttributesHelper.UNPUBLISH_KEY );
            if ( publishAttr != null || unpublishAttr != null )
            {
                final NodeVersion nodeVersion = nodeService.getByNodeVersionKey( nodeVersionMetadata.getNodeVersionKey() );
                final PropertyTree data = nodeVersion.getData();

                final ContentPublishInfo publishInfo = PublishInfoSerializer.serialize( data.getSet( ContentPropertyNames.PUBLISH_INFO ) );
                if ( publishAttr != null )
                {
                    builder.published( ContentAttributesHelper.getOpTime( publishAttr ) );
                    builder.publishedBy( ContentAttributesHelper.getUser( publishAttr ) );
                }

                builder.publishedFrom( publishInfo.getFrom() );
                builder.publishedTo( publishInfo.getTo() );

                if ( unpublishAttr != null )
                {
                    builder.unpublished( ContentAttributesHelper.getOpTime( unpublishAttr ) );
                    builder.unpublishedBy( ContentAttributesHelper.getUser( unpublishAttr ) );
                }
            }

            attributes.list()
                .stream()
                .filter( v -> ContentAttributesHelper.CHANGE_KEYS.contains( v.property( Attributes.KEY_PROPERTY ).asString() ) )
                .reduce( ( first, second ) -> second )
                .ifPresent( changeAttr -> {
                    builder.changedBy( ContentAttributesHelper.getUser( changeAttr ) );
                    builder.changeFields( changeAttr.optional( "fields" ).map( GenericValue::asStringList ).orElse( List.of() ) );
                    builder.change( changeAttr.property( Attributes.KEY_PROPERTY ).asString() );
                } );
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
