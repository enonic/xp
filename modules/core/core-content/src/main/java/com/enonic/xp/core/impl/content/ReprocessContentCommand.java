package com.enonic.xp.core.impl.content;

import java.time.Instant;

import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.Media;
import com.enonic.xp.content.ReprocessContentParams;
import com.enonic.xp.content.UpdateMediaParams;
import com.enonic.xp.media.MediaInfoService;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartDescriptorService;

import static com.enonic.xp.content.ContentPropertyNames.MODIFIED_TIME;


final class ReprocessContentCommand
    extends AbstractCreatingOrUpdatingContentCommand
{
    private final ReprocessContentParams params;

    private final MediaInfoService mediaInfoService;

    private final PageDescriptorService pageDescriptorService;

    private final PartDescriptorService partDescriptorService;

    private final LayoutDescriptorService layoutDescriptorService;

    private ReprocessContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.mediaInfoService = builder.mediaInfoService;
        this.pageDescriptorService = builder.pageDescriptorService;
        this.partDescriptorService = builder.partDescriptorService;
        this.layoutDescriptorService = builder.layoutDescriptorService;
    }

    Content execute()
    {
        final Content source = this.getContent( params.getContentId() );

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
        final ByteSource binary = GetBinaryCommand.create( id, source.getBinaryReference(), this ).build().execute();
        final UpdateMediaParams updateMediaParams = new UpdateMediaParams().
            byteSource( binary ).
            mimeType( source.getMimeType() ).
            content( id ).
            name( source.getName() );

        return UpdateMediaCommand.create( updateMediaParams, this ).
            mediaInfoService( mediaInfoService ).
            siteService( this.siteService ).
            contentTypeService( this.contentTypeService ).
            pageDescriptorService( this.pageDescriptorService ).
            partDescriptorService( this.partDescriptorService ).
            layoutDescriptorService( this.layoutDescriptorService ).
            build().execute();
    }

    private Content revertModifiedTime( final Content content, final Instant modifiedTime )
    {
        final UpdateNodeParams update = UpdateNodeParams.create().
            id( NodeId.from( content.getId() ) ).
            editor( ( node ) -> node.data.getRoot().setInstant( MODIFIED_TIME, modifiedTime ) ).
            build();
        this.nodeService.update( update );
        return this.getContent( content.getId() );
    }

    public static Builder create( final ReprocessContentParams params )
    {
        return new Builder( params );
    }

    public static class Builder
        extends AbstractCreatingOrUpdatingContentCommand.Builder<Builder>
    {
        private final ReprocessContentParams params;

        private MediaInfoService mediaInfoService;

        private PageDescriptorService pageDescriptorService;

        private PartDescriptorService partDescriptorService;

        private LayoutDescriptorService layoutDescriptorService;

        private Builder( final ReprocessContentParams params )
        {
            this.params = params;
        }

        public Builder mediaInfoService( final MediaInfoService value )
        {
            this.mediaInfoService = value;
            return this;
        }

        public Builder pageDescriptorService( final PageDescriptorService value )
        {
            this.pageDescriptorService = value;
            return this;
        }

        public Builder partDescriptorService( final PartDescriptorService value )
        {
            this.partDescriptorService = value;
            return this;
        }

        public Builder layoutDescriptorService( final LayoutDescriptorService value )
        {
            this.layoutDescriptorService = value;
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
