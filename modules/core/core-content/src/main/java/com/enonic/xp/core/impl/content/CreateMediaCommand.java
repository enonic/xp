package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.CreateMediaParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.FormDefaultValuesProcessor;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.media.MediaInfoService;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.schema.content.ContentTypeFromMimeTypeResolver;
import com.enonic.xp.schema.content.ContentTypeName;

final class CreateMediaCommand
    extends AbstractCreatingOrUpdatingContentCommand
{
    private final CreateMediaParams params;

    private final MediaInfoService mediaInfoService;

    private final FormDefaultValuesProcessor formDefaultValuesProcessor;

    private final PageDescriptorService pageDescriptorService;

    private final PartDescriptorService partDescriptorService;

    private final LayoutDescriptorService layoutDescriptorService;

    private CreateMediaCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.mediaInfoService = builder.mediaInfoService;
        this.formDefaultValuesProcessor = builder.formDefaultValuesProcessor;
        this.pageDescriptorService = builder.pageDescriptorService;
        this.partDescriptorService = builder.partDescriptorService;
        this.layoutDescriptorService = builder.layoutDescriptorService;
    }

    Content execute()
    {
        this.params.validate();

        return doExecute();
    }

    private Content doExecute()
    {
        final MediaInfo mediaInfo = mediaInfoService.parseMediaInfo( params.getByteSource() );

        if ( ( params.getMimeType() == null || isBinaryContentType( params.getMimeType() ) ) && mediaInfo.getMediaType() != null )
        {
            params.mimeType( mediaInfo.getMediaType() );
        }

        Preconditions.checkNotNull( params.getMimeType(), "Unable to resolve media type" );

        final ContentTypeName resolvedTypeFromMimeType = ContentTypeFromMimeTypeResolver.resolve( params.getMimeType() );
        final ContentTypeName type = resolvedTypeFromMimeType != null
            ? resolvedTypeFromMimeType
            : isExecutableContentType( params.getMimeType(), params.getName() )
                ? ContentTypeName.executableMedia()
                : ContentTypeName.unknownMedia();

        final PropertyTree data = new PropertyTree();
        new MediaFormDataBuilder().
            type( type ).
            attachment( params.getName() ).
            focalX( params.getFocalX() ).
            focalY( params.getFocalY() ).
            caption( params.getCaption() ).
            artist( params.getArtist() ).
            copyright( params.getCopyright() ).
            tags( params.getTags() ).
            build( data );

        final CreateAttachment mediaAttachment = CreateAttachment.create().
            name( params.getName() ).
            mimeType( params.getMimeType() ).
            label( "source" ).
            byteSource( params.getByteSource() ).
            text( type.isTextualMedia() ? mediaInfo.getTextContent() : "" ).
            build();

        final CreateContentParams createContentParams = CreateContentParams.create().
            name( params.getName() ).
            parent( params.getParent() ).
            requireValid( true ).
            type( type ).
            displayName( trimExtension( params.getName() ) ).
            contentData( data ).
            createAttachments( CreateAttachments.from( mediaAttachment ) ).
            inheritPermissions( true ).
            build();

        final CreateContentCommand createCommand = CreateContentCommand.create( this ).
            mediaInfo( mediaInfo ).
            params( createContentParams ).
            siteService( this.siteService ).
            mixinService( this.mixinService ).
            formDefaultValuesProcessor( this.formDefaultValuesProcessor ).
            pageDescriptorService( this.pageDescriptorService ).
            partDescriptorService( this.partDescriptorService ).
            layoutDescriptorService( this.layoutDescriptorService ).
            build();

        return createCommand.execute();
    }

    private String trimExtension( final String name )
    {
        if ( name.lastIndexOf( "." ) < 0 )
        {
            return name;
        }
        return name.substring( 0, name.lastIndexOf( "." ) );
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

        private FormDefaultValuesProcessor formDefaultValuesProcessor;

        private PageDescriptorService pageDescriptorService;

        private PartDescriptorService partDescriptorService;

        private LayoutDescriptorService layoutDescriptorService;


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

        public Builder formDefaultValuesProcessor( final FormDefaultValuesProcessor formDefaultValuesProcessor )
        {
            this.formDefaultValuesProcessor = formDefaultValuesProcessor;
            return this;
        }

        Builder pageDescriptorService( final PageDescriptorService value )
        {
            this.pageDescriptorService = value;
            return this;
        }

        Builder partDescriptorService( final PartDescriptorService value )
        {
            this.partDescriptorService = value;
            return this;
        }

        Builder layoutDescriptorService( final LayoutDescriptorService value )
        {
            this.layoutDescriptorService = value;
            return this;
        }

        @Override
        void validate()
        {
            Preconditions.checkNotNull( params, "params must be given" );
            Preconditions.checkNotNull( formDefaultValuesProcessor );
            super.validate();
        }

        public CreateMediaCommand build()
        {
            validate();
            return new CreateMediaCommand( this );
        }
    }

}
