package com.enonic.wem.core.content;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.versioning.ContentVersion;
import com.enonic.wem.api.content.versioning.ContentVersions;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityVersion;
import com.enonic.wem.api.entity.EntityVersions;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.api.entity.Nodes;

public class GetContentVersionsCommand
    extends AbstractContentCommand
{
    private final ContentId contentId;

    private final int from;

    private final int size;

    private GetContentVersionsCommand( Builder builder )
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
        final Contents contents = getContentVersions();

        final ContentVersions.Builder versionsBuilder = ContentVersions.create();

        for ( final Content content : contents )
        {
            versionsBuilder.add( ContentVersion.create().
                displayName( content.getDisplayName() ).
                modified( content.getModifiedTime() ).
                modifier( content.getModifier() ).
                comment( "dummyComment" ).
                build() );
        }

        return versionsBuilder.build();
    }

    private Contents getContentVersions()
    {
        final EntityId entityId = EntityId.from( this.contentId );

        final EntityVersions entityVersions = nodeService.getVersions( entityId, this.context );

        final Nodes.Builder builder = Nodes.create();

        for ( final EntityVersion entityVersion : entityVersions )
        {
            builder.add( nodeService.getByBlobKey( entityVersion.getBlobKey(), this.context ) );
        }

        final Nodes build = builder.build();

        return translator.fromNodes( build );
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
