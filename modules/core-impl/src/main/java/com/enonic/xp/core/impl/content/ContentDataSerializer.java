package com.enonic.xp.core.impl.content;

import java.io.IOException;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.core.content.Content;
import com.enonic.xp.core.content.ContentPropertyNames;
import com.enonic.xp.core.content.CreateContentTranslatorParams;
import com.enonic.xp.core.content.Metadata;
import com.enonic.xp.core.content.Metadatas;
import com.enonic.xp.core.content.UpdateContentTranslatorParams;
import com.enonic.xp.core.content.attachment.Attachment;
import com.enonic.xp.core.content.attachment.AttachmentNames;
import com.enonic.xp.core.content.attachment.Attachments;
import com.enonic.xp.core.content.attachment.CreateAttachment;
import com.enonic.xp.core.content.attachment.CreateAttachments;
import com.enonic.xp.core.data.PropertySet;
import com.enonic.xp.core.data.PropertyTree;
import com.enonic.xp.core.schema.content.ContentTypeName;
import com.enonic.xp.core.schema.mixin.Mixin;
import com.enonic.xp.core.schema.mixin.MixinName;
import com.enonic.xp.core.schema.mixin.MixinService;
import com.enonic.xp.core.security.PrincipalKey;
import com.enonic.xp.core.icon.Thumbnail;
import com.enonic.xp.core.util.BinaryReference;
import com.enonic.xp.core.util.Exceptions;
import com.enonic.xp.core.impl.content.page.PageDataSerializer;

import static com.enonic.xp.core.content.ContentPropertyNames.ATTACHMENT;
import static com.enonic.xp.core.content.ContentPropertyNames.CREATED_TIME;
import static com.enonic.xp.core.content.ContentPropertyNames.CREATOR;
import static com.enonic.xp.core.content.ContentPropertyNames.DATA;
import static com.enonic.xp.core.content.ContentPropertyNames.DISPLAY_NAME;
import static com.enonic.xp.core.content.ContentPropertyNames.EXTRA_DATA;
import static com.enonic.xp.core.content.ContentPropertyNames.LANGUAGE;
import static com.enonic.xp.core.content.ContentPropertyNames.MODIFIED_TIME;
import static com.enonic.xp.core.content.ContentPropertyNames.MODIFIER;
import static com.enonic.xp.core.content.ContentPropertyNames.OWNER;
import static com.enonic.xp.core.content.ContentPropertyNames.PAGE;
import static com.enonic.xp.core.content.ContentPropertyNames.TYPE;
import static com.enonic.xp.core.content.ContentPropertyNames.VALID;

public final class ContentDataSerializer
{
    private final static Logger LOG = LoggerFactory.getLogger( ContentDataSerializer.class );

    private static final PageDataSerializer PAGE_SERIALIZER = new PageDataSerializer( ContentPropertyNames.PAGE );

    private final MixinService mixinService;

    public ContentDataSerializer( final MixinService mixinService )
    {
        this.mixinService = mixinService;
    }

    public PropertyTree toNodeData( final UpdateContentTranslatorParams params )
    {
        final PropertyTree newPropertyTree = new PropertyTree();
        final PropertySet contentAsData = newPropertyTree.getRoot();

        final Content content = params.getEditedContent();
        final CreateAttachments createAttachments = params.getCreateAttachments();

        contentAsData.setBoolean( ContentPropertyNames.VALID, content.isValid() );
        contentAsData.ifNotNull().addString( DISPLAY_NAME, content.getDisplayName() );
        contentAsData.ifNotNull().addString( TYPE, content.getType().toString() );
        contentAsData.ifNotNull().addString( OWNER, content.getOwner() != null ? content.getOwner().toString() : null );
        contentAsData.ifNotNull().addString( LANGUAGE, content.getLanguage() != null ? content.getLanguage().toLanguageTag() : null );
        contentAsData.ifNotNull().addInstant( MODIFIED_TIME, content.getModifiedTime() );
        contentAsData.ifNotNull().addString( MODIFIER, params.getModifier().toString() );
        contentAsData.ifNotNull().addString( CREATOR, content.getCreator().toString() );
        contentAsData.ifNotNull().addInstant( CREATED_TIME, content.getCreatedTime() );
        contentAsData.addSet( DATA, content.getData().getRoot().copy( contentAsData.getTree() ) );

        if ( content.hasMetadata() )
        {
            final PropertySet metadataSet = contentAsData.addSet( ContentPropertyNames.EXTRA_DATA );

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

        return newPropertyTree;
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
        contentAsData.ifNotNull().addString( OWNER, PrincipalKey.ofAnonymous().equals( params.getOwner() ) || params.getOwner() == null
            ? null
            : params.getOwner().toString() );
        contentAsData.ifNotNull().addString( LANGUAGE, params.getLanguage() != null ? params.getLanguage().toLanguageTag() : null );
        contentAsData.addSet( DATA, params.getData().getRoot().copy( contentAsData.getTree() ) );

        if ( params.getMetadata() != null && !params.getMetadata().isEmpty() )
        {
            final PropertySet metaSet = contentAsData.addSet( EXTRA_DATA );
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
        final PropertySet metadataSet = contentAsSet.getSet( EXTRA_DATA );
        if ( metadataSet != null )
        {
            final Metadatas.Builder metadatasBuilder = Metadatas.builder();
            for ( final String metadataStringName : metadataSet.getPropertyNames() )
            {
                final MixinName metadataName = metadataStringName.contains( MixinName.SEPARATOR )
                    ? resolveMetadataByMixinName( MixinName.from( metadataStringName ) )
                    : resolveMetadataByLocalName( metadataStringName );
                if ( metadataName != null )
                {
                    metadatasBuilder.add( new Metadata( metadataName, metadataSet.getSet( metadataStringName ).toTree() ) );
                }
                else
                {
                    LOG.warn( "Mixin [" + metadataStringName + "] could not be found" );
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

    private MixinName resolveMetadataByLocalName( final String metadataLocalName )
    {
        final Mixin mixin = mixinService.getByLocalName( metadataLocalName );
        return mixin != null ? mixin.getName() : null;
    }

    private MixinName resolveMetadataByMixinName( final MixinName name )
    {
        final Mixin mixin = mixinService.getByName( name );
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
