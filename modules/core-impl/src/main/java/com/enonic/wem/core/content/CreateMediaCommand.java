package com.enonic.wem.core.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.CreateContentParams;
import com.enonic.wem.api.content.CreateMediaParams;
import com.enonic.wem.api.content.attachment.CreateAttachment;
import com.enonic.wem.api.content.attachment.CreateAttachments;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.media.MediaInfo;
import com.enonic.wem.api.media.MediaInfoService;
import com.enonic.wem.api.schema.content.ContentTypeName;

final class CreateMediaCommand
    extends AbstractCreatingOrUpdatingContentCommand
{
    private final CreateMediaParams params;

    private final MediaInfoService mediaInfoService;

    private CreateMediaCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.mediaInfoService = builder.mediaInfoService;
    }

    Content execute()
    {
        this.params.validate();

        return doExecute();
    }

    private Content doExecute()
    {
        final MediaInfo mediaInfo = mediaInfoService.parseMediaInfo( params.getByteSource() );
        if ( params.getMimeType() == null && mediaInfo.getMediaType() != null )
        {
            params.mimeType( mediaInfo.getMediaType() );
        }

        Preconditions.checkNotNull( params.getMimeType(), "Unable to resolve media type" );

        final ContentTypeName resolvedTypeFromMimeType = ContentTypeFromMimeTypeResolver.resolve( params.getMimeType() );
        final ContentTypeName type = resolvedTypeFromMimeType != null ? resolvedTypeFromMimeType : ContentTypeName.unknownMedia();

        // TODO: Resolve form based on type?
        final PropertyTree data = new PropertyTree();
        new ImageFormDataBuilder().
            image( params.getName() ).
            mimeType( params.getMimeType() ).
            build( data );

        final CreateAttachment mediaAttachment = CreateAttachment.create().
            name( params.getName() ).
            mimeType( params.getMimeType() ).
            label( "source" ).
            byteSource( params.getByteSource() ).
            build();

        final CreateContentParams createContentParams = new CreateContentParams().
            name( params.getName() ).
            parent( params.getParent() ).
            requireValid( true ).
            type( type ).
            displayName( params.getName() ).
            contentData( data ).
            createAttachments( CreateAttachments.from( mediaAttachment ) );

        final CreateContentCommand createCommand = CreateContentCommand.create( this ).
            mediaInfo( mediaInfo ).
            params( createContentParams ).
            moduleService( this.moduleService ).
            mixinService( this.mixinService ).
            build();

        return createCommand.execute();
    }


    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractCreatingOrUpdatingContentCommand.Builder<Builder>
    {
        private CreateMediaParams params;

        private MediaInfoService mediaInfoService;

        public Builder params( final CreateMediaParams params )
        {
            this.params = params;
            return this;
        }

        public Builder mediaInfoService( final MediaInfoService value )
        {
            this.mediaInfoService = value;
            return this;
        }

        void validate()
        {
            Preconditions.checkNotNull( params, "params must be given" );
            super.validate();
        }

        public CreateMediaCommand build()
        {
            validate();
            return new CreateMediaCommand( this );
        }
    }

}
