package com.enonic.wem.core.content;

import java.io.IOException;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.CreateContentParams;
import com.enonic.wem.api.content.Metadata;
import com.enonic.wem.api.content.Metadatas;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.AttachmentNames;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.wem.api.content.attachment.CreateAttachment;
import com.enonic.wem.api.content.attachment.CreateAttachments;
import com.enonic.wem.api.data.PropertySet;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.metadata.MetadataSchemaName;
import com.enonic.wem.api.thumb.Thumbnail;
import com.enonic.wem.api.util.BinaryReference;
import com.enonic.wem.api.util.Exceptions;
import com.enonic.wem.core.content.page.PageDataSerializer;
import com.enonic.wem.core.form.FormDataSerializer;

public class ContentDataSerializer
{
    private static final FormDataSerializer FORM_SERIALIZER = new FormDataSerializer( ContentPropertyNames.FORM );

    private static final PageDataSerializer PAGE_SERIALIZER = new PageDataSerializer( ContentPropertyNames.PAGE );

    public void toData( final Content content, final PropertySet contentAsData, final CreateAttachments createAttachments )
    {
        contentAsData.setBoolean( ContentPropertyNames.DRAFT, content.isDraft() );
        contentAsData.ifNotNull().addString( ContentPropertyNames.DISPLAY_NAME, content.getDisplayName() );
        contentAsData.ifNotNull().addString( ContentPropertyNames.TYPE, content.getType().toString() );

        contentAsData.addSet( ContentPropertyNames.DATA, content.getData().getRoot().copy( contentAsData.getTree() ) );

        if ( content.hasMetadata() )
        {
            final PropertySet metadataSet = contentAsData.addSet( ContentPropertyNames.METADATA );
            for ( final Metadata metadata : content.getAllMetadata() )
            {
                metadataSet.addSet( metadata.getName().toString(), metadata.getData().getRoot().copy( contentAsData.getTree() ) );
            }
        }

        if ( createAttachments != null )
        {
            createAttachmentsToData( createAttachments, contentAsData );
        }

        if ( content.getForm() != null )
        {
            FORM_SERIALIZER.toData( content.getForm(), contentAsData );
        }

        if ( content.hasPage() )
        {
            PAGE_SERIALIZER.toData( content.getPage(), contentAsData );
        }
    }

    public Content.Builder fromData( final PropertySet contentAsSet )
    {
        final ContentTypeName contentTypeName = ContentTypeName.from( contentAsSet.getString( ContentPropertyNames.TYPE ) );
        final Content.Builder builder = Content.newContent( contentTypeName );

        builder.displayName( contentAsSet.getString( ContentPropertyNames.DISPLAY_NAME ) );
        builder.draft( contentAsSet.getBoolean( ContentPropertyNames.DRAFT ) );
        builder.data( contentAsSet.getSet( ContentPropertyNames.DATA ).toTree() );

        final PropertySet metadataSet = contentAsSet.getSet( ContentPropertyNames.METADATA );
        if ( metadataSet != null )
        {
            final Metadatas.Builder metadatasBuilder = Metadatas.builder();
            for ( final String metadataName : metadataSet.getPropertyNames() )
            {
                metadatasBuilder.add(
                    new Metadata( MetadataSchemaName.from( metadataName ), metadataSet.getSet( metadataName ).toTree() ) );
            }
            builder.metadata( metadatasBuilder.build() );
        }

        builder.form( FORM_SERIALIZER.fromData( contentAsSet.getSet( ContentPropertyNames.FORM ) ) );
        if ( contentAsSet.hasProperty( ContentPropertyNames.PAGE ) )
        {
            builder.page( PAGE_SERIALIZER.fromData( contentAsSet.getSet( ContentPropertyNames.PAGE ) ) );
        }

        final Attachments attachments = dataToAttachments( contentAsSet.getSets( ContentPropertyNames.ATTACHMENT ) );
        builder.attachments( attachments );

        final Attachment thumbnailAttachment = attachments.byName( AttachmentNames.THUMBNAIL );
        if ( thumbnailAttachment != null )
        {
            final BinaryReference thumbnailBinaryRef = thumbnailAttachment.getBinaryReference();
            final Thumbnail thumbnail =
                Thumbnail.from( thumbnailBinaryRef, thumbnailAttachment.getMimeType(), thumbnailAttachment.getSize() );
            builder.thumbnail( thumbnail );
        }

        return builder;
    }

    void toData( final CreateContentParams params, final PropertySet contentAsData )
    {
        contentAsData.addBoolean( ContentPropertyNames.DRAFT, params.isDraft() );
        contentAsData.ifNotNull().addString( ContentPropertyNames.DISPLAY_NAME, params.getDisplayName() );
        contentAsData.ifNotNull().addString( ContentPropertyNames.TYPE, params.getType() != null ? params.getType().toString() : null );

        contentAsData.addSet( ContentPropertyNames.DATA, params.getData().getRoot().copy( contentAsData.getTree() ) );

        if ( params.getMetadata() != null && !params.getMetadata().isEmpty() )
        {
            final PropertySet metadataSet = contentAsData.addSet( ContentPropertyNames.METADATA );
            for ( final Metadata metadata : params.getMetadata() )
            {
                metadataSet.addSet( metadata.getName().toString(), metadata.getData().getRoot().copy( metadataSet.getTree() ) );
            }
        }

        if ( params.getCreateAttachments() != null )
        {
            createAttachmentsToData( params.getCreateAttachments(), contentAsData );
        }

        if ( params.getForm() != null )
        {
            FORM_SERIALIZER.toData( params.getForm(), contentAsData );
        }
    }

    Attachments dataToAttachments( final Iterable<PropertySet> attachmentSets )
    {
        final Attachments.Builder attachments = Attachments.builder();
        for ( final PropertySet attachmentAsSet : attachmentSets )
        {
            attachments.add( Attachment.newAttachment().
                name( attachmentAsSet.getString( "name" ) ).
                label( attachmentAsSet.getString( "label" ) ).
                mimeType( attachmentAsSet.getString( "mimeType" ) ).
                size( attachmentAsSet.getLong( "size" ) ).
                build() );
        }
        return attachments.build();
    }

    void createAttachmentsToData( final CreateAttachments createAttachments, final PropertySet contentAsData )
    {
        for ( final CreateAttachment createAttachment : createAttachments )
        {
            final PropertySet attachmentSet = contentAsData.addSet( ContentPropertyNames.ATTACHMENT );
            attachmentSet.addString( "name", createAttachment.getName() );
            attachmentSet.addString( "label", createAttachment.getLabel() );
            attachmentSet.addBinaryReference( "binary", createAttachment.getBinaryReference() );
            attachmentSet.addString( "mimeType", createAttachment.getMimeType() );

            try
            {
                attachmentSet.addLong( "size", createAttachment.getByteSource().size() );
            }
            catch ( IOException e )
            {
                throw Exceptions.unchecked( e );
            }
        }
    }
}
