package com.enonic.wem.core.content;

import java.io.IOException;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPropertyNames;
import com.enonic.wem.api.content.CreateContentTranslatorParams;
import com.enonic.wem.api.content.Metadata;
import com.enonic.wem.api.content.Metadatas;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.AttachmentNames;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.wem.api.content.attachment.CreateAttachment;
import com.enonic.wem.api.content.attachment.CreateAttachments;
import com.enonic.wem.api.data.PropertySet;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.thumb.Thumbnail;
import com.enonic.wem.api.util.BinaryReference;
import com.enonic.wem.api.util.Exceptions;
import com.enonic.wem.core.content.page.PageDataSerializer;

import static com.enonic.wem.api.content.ContentPropertyNames.ATTACHMENT;
import static com.enonic.wem.api.content.ContentPropertyNames.CREATED_TIME;
import static com.enonic.wem.api.content.ContentPropertyNames.CREATOR;
import static com.enonic.wem.api.content.ContentPropertyNames.DATA;
import static com.enonic.wem.api.content.ContentPropertyNames.DISPLAY_NAME;
import static com.enonic.wem.api.content.ContentPropertyNames.LANGUAGE;
import static com.enonic.wem.api.content.ContentPropertyNames.META_STEPS;
import static com.enonic.wem.api.content.ContentPropertyNames.MODIFIED_TIME;
import static com.enonic.wem.api.content.ContentPropertyNames.MODIFIER;
import static com.enonic.wem.api.content.ContentPropertyNames.OWNER;
import static com.enonic.wem.api.content.ContentPropertyNames.PAGE;
import static com.enonic.wem.api.content.ContentPropertyNames.TYPE;
import static com.enonic.wem.api.content.ContentPropertyNames.VALID;

public final class ContentDataSerializer
{
    private final static Logger LOG = LoggerFactory.getLogger( ContentDataSerializer.class );

    private static final PageDataSerializer PAGE_SERIALIZER = new PageDataSerializer( ContentPropertyNames.PAGE );

    private final MixinService mixinService;

    public ContentDataSerializer( final MixinService mixinService )
    {
        this.mixinService = mixinService;
    }

    public void populatedEditedProperties( final Content content, final PropertySet contentAsData,
                                           final CreateAttachments createAttachments )
    {
        contentAsData.setBoolean( ContentPropertyNames.VALID, content.isValid() );
        contentAsData.ifNotNull().addString( DISPLAY_NAME, content.getDisplayName() );
        contentAsData.ifNotNull().addString( TYPE, content.getType().toString() );
        contentAsData.ifNotNull().addString( OWNER, content.getOwner() != null ? content.getOwner().toString() : null );
        contentAsData.ifNotNull().addString( LANGUAGE, content.getLanguage() != null ? content.getLanguage().toLanguageTag() : null );
        contentAsData.ifNotNull().addInstant( MODIFIED_TIME, content.getModifiedTime() );
        contentAsData.ifNotNull().addString( MODIFIER, content.getModifier().toString() );
        contentAsData.ifNotNull().addString( CREATOR, content.getCreator().toString() );
        contentAsData.ifNotNull().addInstant( CREATED_TIME, content.getCreatedTime() );
        contentAsData.addSet( DATA, content.getData().getRoot().copy( contentAsData.getTree() ) );

        if ( content.hasMetadata() )
        {
            final PropertySet metadataSet = contentAsData.addSet( ContentPropertyNames.META_STEPS );

            for ( final Metadata metadata : content.getAllMetadata() )
            {
                metadataSet.addSet( metadata.getName().getLocalName(), metadata.getData().getRoot().copy( contentAsData.getTree() ) );
            }
        }

        if ( createAttachments != null )
        {
            applyCreateAttachments( createAttachments, contentAsData );
        }
        else
        {
            applyAttachmentsAsData( content.getAttachments(), contentAsData );
        }

        if ( content.hasPage() )
        {
            PAGE_SERIALIZER.toData( content.getPage(), contentAsData );
        }
    }

    void toCreateNodeData( final CreateContentTranslatorParams params, final PropertySet contentAsData )
    {
        contentAsData.addBoolean( VALID, params.isValid() );
        contentAsData.ifNotNull().addString( DISPLAY_NAME, params.getDisplayName() );
        contentAsData.ifNotNull().addString( TYPE, params.getType() != null ? params.getType().toString() : null );
        contentAsData.ifNotNull().addInstant( CREATED_TIME, params.getCreatedTime() );
        contentAsData.ifNotNull().addString( CREATOR, params.getCreator().toString() );
        contentAsData.ifNotNull().addString( MODIFIER, params.getModifier().toString() );
        contentAsData.ifNotNull().addInstant( MODIFIED_TIME, params.getModifiedTime() );
        contentAsData.ifNotNull().addString( ContentPropertyNames.OWNER,
                                             PrincipalKey.ofAnonymous().equals( params.getOwner() ) || params.getOwner() == null
                                                 ? null
                                                 : params.getOwner().toString() );
        contentAsData.addSet( DATA, params.getData().getRoot().copy( contentAsData.getTree() ) );

        if ( params.getMetadata() != null && !params.getMetadata().isEmpty() )
        {
            final PropertySet metaSet = contentAsData.addSet( META_STEPS );
            for ( final Metadata metadata : params.getMetadata() )
            {
                metaSet.addSet( metadata.getName().toString(), metadata.getData().getRoot().copy( metaSet.getTree() ) );
            }
        }

        if ( params.getCreateAttachments() != null )
        {
            applyCreateAttachments( params.getCreateAttachments(), contentAsData );
        }
    }

    public Content.Builder fromData( final PropertySet contentAsSet )
    {
        final ContentTypeName contentTypeName = ContentTypeName.from( contentAsSet.getString( ContentPropertyNames.TYPE ) );
        final Content.Builder builder = Content.newContent( contentTypeName );

        builder.displayName( contentAsSet.getString( DISPLAY_NAME ) );
        builder.valid( contentAsSet.getBoolean( VALID ) != null ? contentAsSet.getBoolean( ContentPropertyNames.VALID ) : false );
        builder.data( contentAsSet.getSet( DATA ).toTree() );

        addUserInfo( contentAsSet, builder );
        addOwner( contentAsSet, builder );
        addLanguage( contentAsSet, builder );
        addMetadata( contentAsSet, builder );
        addPage( contentAsSet, builder );
        addAttachments( contentAsSet, builder );

        return builder;
    }

    private void addUserInfo( final PropertySet contentAsSet, final Content.Builder builder )
    {
        builder.creator( PrincipalKey.from( contentAsSet.getString( CREATOR ) ) );
        builder.createdTime( contentAsSet.getInstant( CREATED_TIME ) );
        builder.modifier( contentAsSet.getString( MODIFIER ) != null ? PrincipalKey.from( contentAsSet.getString( MODIFIER ) ) : null );
        builder.modifiedTime( contentAsSet.getInstant( MODIFIED_TIME ) != null ? contentAsSet.getInstant( MODIFIED_TIME ) : null );
    }

    private void addAttachments( final PropertySet contentAsSet, final Content.Builder builder )
    {
        final Attachments attachments = dataToAttachments( contentAsSet.getSets( ATTACHMENT ) );
        builder.attachments( attachments );

        final Attachment thumbnailAttachment = attachments.byName( AttachmentNames.THUMBNAIL );
        if ( thumbnailAttachment != null )
        {
            final BinaryReference thumbnailBinaryRef = thumbnailAttachment.getBinaryReference();
            final Thumbnail thumbnail =
                Thumbnail.from( thumbnailBinaryRef, thumbnailAttachment.getMimeType(), thumbnailAttachment.getSize() );
            builder.thumbnail( thumbnail );
        }
    }

    private void addPage( final PropertySet contentAsSet, final Content.Builder builder )
    {
        if ( contentAsSet.hasProperty( PAGE ) )
        {
            builder.page( PAGE_SERIALIZER.fromData( contentAsSet.getSet( PAGE ) ) );
        }
    }

    private void addMetadata( final PropertySet contentAsSet, final Content.Builder builder )
    {
        final PropertySet metadataSet = contentAsSet.getSet( META_STEPS );
        if ( metadataSet != null )
        {
            final Metadatas.Builder metadatasBuilder = Metadatas.builder();
            for ( final String metadataLocalName : metadataSet.getPropertyNames() )
            {
                final MixinName metadataName = resolveMetadataName( metadataLocalName );
                if ( metadataName != null )
                {
                    metadatasBuilder.add( new Metadata( metadataName, metadataSet.getSet( metadataLocalName ).toTree() ) );
                }
                else
                {
                    LOG.warn( "Mixin [%s] could not be found", metadataLocalName );
                }
            }
            builder.metadata( metadatasBuilder.build() );
        }
    }

    private void addLanguage( final PropertySet contentAsSet, final Content.Builder builder )
    {
        String language = contentAsSet.getString( LANGUAGE );
        if ( StringUtils.isNotEmpty( language ) )
        {
            builder.language( Locale.forLanguageTag( language ) );
        }
    }

    private void addOwner( final PropertySet contentAsSet, final Content.Builder builder )
    {
        String owner = contentAsSet.getString( OWNER );

        if ( StringUtils.isNotBlank( owner ) )
        {
            builder.owner( PrincipalKey.from( owner ) );
        }
    }

    private MixinName resolveMetadataName( final String metadataLocalName )
    {
        final Mixin mixin = mixinService.getByLocalName( metadataLocalName );
        return mixin != null ? mixin.getName() : null;
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

    private void applyCreateAttachments( final CreateAttachments createAttachments, final PropertySet contentAsData )
    {
        for ( final CreateAttachment createAttachment : createAttachments )
        {
            final PropertySet attachmentSet = contentAsData.addSet( ATTACHMENT );
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

    private void applyAttachmentsAsData( final Attachments attachments, final PropertySet contentAsData )
    {
        for ( final Attachment attachment : attachments )
        {
            final PropertySet attachmentSet = contentAsData.addSet( ATTACHMENT );
            attachmentSet.addString( "name", attachment.getName() );
            attachmentSet.addString( "label", attachment.getLabel() );
            attachmentSet.addBinaryReference( "binary", attachment.getBinaryReference() );
            attachmentSet.addString( "mimeType", attachment.getMimeType() );
            attachmentSet.addLong( "size", attachment.getSize() );
        }
    }
}
