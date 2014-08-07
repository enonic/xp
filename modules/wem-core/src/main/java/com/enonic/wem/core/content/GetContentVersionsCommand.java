package com.enonic.wem.core.content;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.versioning.ContentVersion;
import com.enonic.wem.api.content.versioning.ContentVersions;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityVersion;
import com.enonic.wem.api.entity.EntityVersions;
import com.enonic.wem.api.entity.GetEntityVersionsParams;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.Nodes;

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


    public ContentVersions execute()
    {
        return doGetContentVersions();
    }

    private ContentVersions doGetContentVersions()
    {
        final EntityId entityId = EntityId.from( this.contentId );

        final EntityVersions entityVersions = nodeService.getVersions( GetEntityVersionsParams.create().
            entityId( entityId ).
            from( this.from ).
            size( this.size ).
            build(), this.context );

        final Nodes.Builder builder = Nodes.create();

        for ( final EntityVersion entityVersion : entityVersions )
        {
            builder.add( nodeService.getByBlobKey( entityVersion.getBlobKey(), this.context ) );
        }

        final Nodes nodes = builder.build();

        final ContentVersions.Builder contentVersions = ContentVersions.create().
            contentId( ContentId.from( entityId ) );

        for ( final Node node : nodes )
        {
            final Content content = translator.fromNode( node );

            contentVersions.add( ContentVersion.create().
                comment( "Dummy comment" ).
                displayName( content.getDisplayName() ).
                modified( content.getModifiedTime() ).
                modifier( content.getModifier() ).
                build() );
        }

        return contentVersions.build();
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
