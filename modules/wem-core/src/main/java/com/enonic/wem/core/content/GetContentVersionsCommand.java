package com.enonic.wem.core.content;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.versioning.ContentVersion;
import com.enonic.wem.api.content.versioning.ContentVersions;
import com.enonic.wem.api.content.versioning.FindContentVersionsResult;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityVersion;
import com.enonic.wem.api.entity.FindEntityVersionsResult;
import com.enonic.wem.api.entity.GetEntityVersionsParams;
import com.enonic.wem.api.entity.Node;

public class GetContentVersionsCommand
    extends AbstractContentCommand
{
    private final ContentId contentId;

    private final int from;

    private final int size;

    private GetContentVersionsCommand( final Builder builder )
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
        final EntityId entityId = EntityId.from( this.contentId );

        final FindEntityVersionsResult findEntityVersionsResult = nodeService.getVersions( GetEntityVersionsParams.create().
            entityId( entityId ).
            from( this.from ).
            size( this.size ).
            build(), this.context );

        final FindContentVersionsResult.Builder findContentVersionsResultBuilder = FindContentVersionsResult.create();
        findContentVersionsResultBuilder.hits( findEntityVersionsResult.getHits() );
        findContentVersionsResultBuilder.totalHits( findEntityVersionsResult.getTotalHits() );
        findContentVersionsResultBuilder.from( findEntityVersionsResult.getFrom() );
        findContentVersionsResultBuilder.size( findEntityVersionsResult.getSize() );

        final Set<Node> nodeVersions = Sets.newLinkedHashSet();

        for ( final EntityVersion entityVersion : findEntityVersionsResult.getEntityVersions() )
        {
            nodeVersions.add( nodeService.getByBlobKey( entityVersion.getBlobKey(), this.context ) );
        }

        final ContentVersions contentVersions = buildContentVersions( entityId, nodeVersions );

        findContentVersionsResultBuilder.contentVersions( contentVersions );

        return findContentVersionsResultBuilder.build();
    }

    private ContentVersions buildContentVersions( final EntityId entityId, final Collection<Node> nodes )
    {
        final ContentVersions.Builder contentVersionsBuilder = ContentVersions.create().
            contentId( ContentId.from( entityId ) );

        for ( final Node node : nodes )
        {
            final Content content = translator.fromNode( node );

            contentVersionsBuilder.add( ContentVersion.create().
                comment( "Dummy comment" ).
                displayName( content.getDisplayName() ).
                modified( content.getModifiedTime() ).
                modifier( content.getModifier() ).
                build() );
        }

        return contentVersionsBuilder.build();
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

        public GetContentVersionsCommand build()
        {
            return new GetContentVersionsCommand( this );
        }
    }
}
