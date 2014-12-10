package com.enonic.wem.core.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.Name;
import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.CreateContentParams;
import com.enonic.wem.api.content.CreateMediaParams;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.schema.content.ContentTypeName;

final class CreateMediaCommand
    extends AbstractContentCommand
{
    private final CreateMediaParams params;

    private CreateMediaCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    Content execute()
    {
        this.params.validate();

        return doExecute();
    }

    private Content doExecute()
    {
        final ContentTypeName contentType = ContentTypeFromMimeTypeResolver.resolve( params.getMimeType() );
        if ( contentType == null )
        {
            throw new IllegalArgumentException( "Could not resolve a ContentType from MIME type: " + params.getMimeType() );
        }

        final Blob mediaBlob = blobService.create( params.getInputStream() );
        final Attachment mediaAttachment = Attachment.newAttachment().
            name( params.getName() ).
            mimeType( params.getMimeType() ).
            label( "source" ).
            blobKey( mediaBlob.getKey() ).
            size( mediaBlob.getLength() ).
            build();

        final String nameOfContent = Name.ensureValidName( params.getName() );

        final PropertyTree data = new PropertyTree();
        new ImageFormDataBuilder().
            setName( params.getName() ).
            setMimeType( params.getMimeType() ).
            build( data );

        final CreateContentParams createContentParams = new CreateContentParams();
        createContentParams.contentType( contentType );
        createContentParams.parent( params.getParent() );
        createContentParams.contentData( data );
        createContentParams.displayName( params.getName() );
        createContentParams.name( nameOfContent );
        createContentParams.attachments( mediaAttachment );

        return CreateContentCommand.
            create( this ).
            eventPublisher( this.eventPublisher ).
            params( createContentParams ).
            build().
            execute();
    }


    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private CreateMediaParams params;

        public Builder params( final CreateMediaParams params )
        {
            this.params = params;
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
