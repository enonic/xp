package com.enonic.xp.core.impl.content;

import java.time.Instant;

import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.content.ReprocessContentParams;
import com.enonic.xp.content.UpdateMediaParams;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.UpdateNodeParams;

import static com.enonic.xp.content.ContentPropertyNames.MODIFIED_TIME;


final class ReprocessContentCommand
    extends AbstractContentCommand
{
    private final ReprocessContentParams params;

    private final ContentService contentService;

    private ReprocessContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.contentService = builder.contentService;
    }

    Content execute()
    {
        final Content source = this.contentService.getById( params.getContentId() );

        if ( !source.getType().isDescendantOfMedia() )
        {
            return source;
        }

        final Content reprocessed = reprocessMedia( (Media) source );
        return revertModifiedTime( reprocessed, source.getModifiedTime() );
    }

    private Content reprocessMedia( final Media media )
    {
        final Attachment source = media.getSourceAttachment();
        if ( source == null )
        {
            return media;
        }
        final ContentId id = media.getId();
        final ByteSource binary = contentService.getBinary( id, source.getBinaryReference() );
        final UpdateMediaParams update = new UpdateMediaParams().
            byteSource( binary ).
            mimeType( source.getMimeType() ).
            content( id ).
            name( source.getName() );
        return contentService.update( update );
    }

    private Content revertModifiedTime( final Content content, final Instant modifiedTime )
    {
        final UpdateNodeParams update = UpdateNodeParams.create().
            id( NodeId.from( content.getId() ) ).
            editor( ( node ) -> node.data.getRoot().setInstant( MODIFIED_TIME, modifiedTime ) ).
            build();
        this.nodeService.update( update );
        return contentService.getById( content.getId() );
    }

    public static Builder create( final ReprocessContentParams params )
    {
        return new Builder( params );
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private final ReprocessContentParams params;

        private ContentService contentService;

        private Builder( final ReprocessContentParams params )
        {
            this.params = params;
        }

        public Builder contentService( final ContentService contentService )
        {
            this.contentService = contentService;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
        }

        public ReprocessContentCommand build()
        {
            validate();
            return new ReprocessContentCommand( this );
        }
    }

}
