package com.enonic.xp.core.impl.content;

import java.util.List;
import java.util.Objects;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.content.ContentVersionId;
import com.enonic.xp.content.ContentVersions;
import com.enonic.xp.content.GetContentVersionsParams;
import com.enonic.xp.content.GetContentVersionsResult;
import com.enonic.xp.node.Attributes;
import com.enonic.xp.node.GetNodeVersionsParams;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.GetNodeVersionsResult;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.util.GenericValue;

public class GetContentVersionsCommand
    extends AbstractContentCommand
{
    @NonNull
    private final GetContentVersionsParams params;

    private GetContentVersionsCommand( final Builder builder )
    {
        super( builder );
        this.params = Objects.requireNonNull( builder.params );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public GetContentVersionsResult execute()
    {
        final GetNodeVersionsResult nodeVersionsResult = nodeService.getVersions( GetNodeVersionsParams.create()
                                                                                       .nodeId( NodeId.from( params.getContentId() ) )
                                                                                       .cursor( params.getCursor() )
                                                                                       .size( params.getSize() )
                                                                                       .build() );

        return GetContentVersionsResult.create()
            .totalHits( nodeVersionsResult.getTotalHits() )
            .cursor( nodeVersionsResult.getCursor() )
            .contentVersions( nodeVersionsResult.getNodeVersions()
                                  .stream()
                                  .map( this::createVersion )
                                  .collect( ContentVersions.collector() ) )
            .build();
    }

    public ContentVersion createVersion( final NodeVersion nodeVersion )
    {
        final Attributes attributes = nodeVersion.getAttributes();

        final ContentVersion.Builder builder = ContentVersion.create()
            .contentId( ContentId.from( nodeVersion.getNodeId() ) )
            .versionId( ContentVersionId.from( nodeVersion.getNodeVersionId().toString() ) )
            .path( ContentNodeHelper.translateNodePathToContentPath( nodeVersion.getNodePath() ) )
            .timestamp( nodeVersion.getTimestamp() );

        if ( attributes != null )
        {
            attributes.entrySet()
                .stream()
                .filter( v -> v.getKey().startsWith( "content." ) )
                .map( v -> new ContentVersion.Action( v.getKey(), v.getValue()
                    .optional( "fields" )
                    .map( GenericValue::toStringList )
                    .orElse( List.of() ), ContentAttributesHelper.getUser( v.getValue() ),
                                                      ContentAttributesHelper.getOpTime( v.getValue() ) ) )
                .forEach( builder::addAction );
        }

        if ( nodeVersion.getNodeCommitId() != null )
        {
            final NodeCommitEntry nodeCommitEntry = nodeService.getCommit( nodeVersion.getNodeCommitId() );
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
        @Nullable
        private GetContentVersionsParams params;

        private Builder()
        {
        }

        public Builder params( final GetContentVersionsParams params )
        {
            this.params = params;
            return this;
        }

        public GetContentVersionsCommand build()
        {
            return new GetContentVersionsCommand( this );
        }
    }
}
